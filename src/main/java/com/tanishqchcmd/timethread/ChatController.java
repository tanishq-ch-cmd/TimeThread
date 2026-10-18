package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.crypto.SecretKey;
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

        // 1. Fetch the Master AES-256 Key
        SecretKey masterKey = CryptoEngine.getMasterKey();
        String encryptedContent;

        try {
            // 2. Encrypt the raw text before it touches the database
            encryptedContent = CryptoEngine.encrypt(request.content(), masterKey);
        } catch (Exception e) {
            e.printStackTrace();
            return; // Abort transmission completely if encryption fails
        }

        // 3. Save the encrypted ciphertext to PostgreSQL
        Message message = new Message();
        message.setContent(encryptedContent); // Database only sees ciphertext!
        message.setSender(sender);
        message.setGroup(group);
        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        // 4. Build the WebSocket response using the CLEAN text so the UI can read it live
        ChatMessageResponse response = new ChatMessageResponse(
                message.getId(), request.content(), sender.getName(), sender.getId(), message.getTimestamp()
        );

        // 5. Send it to the active group room
        messagingTemplate.convertAndSend("/topic/group/" + groupId, response);
    }
}