package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private StudentRepository studentRepository;

    // Matches exactly "/topic/group/{id}" — deliberately NOT the "/presence"
    // sub-topic, so subscribing to receive presence updates doesn't itself
    // count as "joining" a group's presence.
    private static final Pattern GROUP_TOPIC_PATTERN = Pattern.compile("^/topic/group/(\\d+)$");

    // sessionId -> which group + student that connection belongs to, so a
    // disconnect event (which has no destination) can still be resolved
    private final Map<String, SessionInfo> sessionsByConnection = new ConcurrentHashMap<>();

    // groupId -> (studentId -> open session count). Reference-counted so a
    // student with two tabs open only goes "offline" once both close.
    private final Map<Long, Map<Long, Integer>> onlineStudentsByGroup = new ConcurrentHashMap<>();

    private record SessionInfo(Long groupId, Long studentId) {}

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        Principal user = accessor.getUser();
        if (destination == null || user == null) return;

        Matcher matcher = GROUP_TOPIC_PATTERN.matcher(destination);
        if (!matcher.matches()) return;

        Long groupId = Long.parseLong(matcher.group(1));
        studentRepository.findByEmail(user.getName()).ifPresent(student -> {
            sessionsByConnection.put(accessor.getSessionId(), new SessionInfo(groupId, student.getId()));
            onlineStudentsByGroup
                    .computeIfAbsent(groupId, g -> new ConcurrentHashMap<>())
                    .merge(student.getId(), 1, Integer::sum);
            broadcastPresence(groupId);
        });
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        SessionInfo info = sessionsByConnection.remove(accessor.getSessionId());
        if (info == null) return;

        Map<Long, Integer> groupMap = onlineStudentsByGroup.get(info.groupId());
        if (groupMap != null) {
            groupMap.merge(info.studentId(), -1, Integer::sum);
            if (groupMap.get(info.studentId()) <= 0) {
                groupMap.remove(info.studentId());
            }
        }
        broadcastPresence(info.groupId());
    }

    private void broadcastPresence(Long groupId) {
        Map<Long, Integer> groupMap = onlineStudentsByGroup.getOrDefault(groupId, Map.of());
        Set<Long> onlineIds = groupMap.keySet();
        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/presence",
                new PresenceUpdate(groupId, onlineIds.size(), onlineIds)
        );
    }
}