package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{groupId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long groupId) {
        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId)
                .stream()
                .map(m -> new ChatMessageResponse(
                        m.getId(), m.getContent(), m.getSender().getName(), m.getSender().getId(), m.getTimestamp()
                ))
                .toList();
    }
}