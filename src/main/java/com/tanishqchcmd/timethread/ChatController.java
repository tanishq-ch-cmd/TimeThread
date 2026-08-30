package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.sendMessage/{groupId}")
    public void sendMessage(@DestinationVariable Long groupId, ChatMessageRequest request, Principal principal) {
        Student sender = studentRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("Unknown sender"));
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Unknown group"));

        Message message = new Message();
        message.setContent(request.content());
        message.setSender(sender);
        message.setGroup(group);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        ChatMessageResponse response = new ChatMessageResponse(
                message.getId(), message.getContent(), sender.getName(), sender.getId(), message.getTimestamp()
        );

        messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
    }
}