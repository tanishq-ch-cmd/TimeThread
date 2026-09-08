package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class DMController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private DirectMessageRepository dmRepository;
    @Autowired
    private StudentRepository studentRepository;

    @MessageMapping("/chat.sendDM")
    public void sendDM(@Payload DMRequest request, Principal principal) {
        Student sender = studentRepository.findByEmail(principal.getName()).orElseThrow();
        Student receiver = studentRepository.findById(request.getReceiverId()).orElseThrow();

        // Save to database
        DirectMessage dm = new DirectMessage();
        dm.setSender(sender);
        dm.setReceiver(receiver);
        dm.setContent(request.getContent());
        dmRepository.save(dm);

        // Create a unique, consistent room ID for these two users (e.g., "3_5")
        Long id1 = Math.min(sender.getId(), receiver.getId());
        Long id2 = Math.max(sender.getId(), receiver.getId());
        String roomName = id1 + "_" + id2;

        // Build the response
        DMResponse response = new DMResponse();
        response.setId(dm.getId());
        response.setSenderId(sender.getId());
        response.setSenderName(sender.getName());
        response.setContent(dm.getContent());

        // Send it only to this specific private room
        messagingTemplate.convertAndSend("/topic/dm/" + roomName, response);
    }

    // --- Data Transfer Objects (DTOs) ---
    public static class DMRequest {
        private Long receiverId;
        private String content;

        public Long getReceiverId() { return receiverId; }
        public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public static class DMResponse {
        private Long id;
        private Long senderId;
        private String senderName;
        private String content;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}