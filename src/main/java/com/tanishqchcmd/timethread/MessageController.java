package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping("/{groupId}/messages")
    public List<ChatMessageResponse> getMessages(@PathVariable Long groupId) {
        SecretKey masterKey = CryptoEngine.getMasterKey();

        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId)
                .stream()
                .map(m -> {
                    String cleanText;
                    try {
                        // Decrypt the database ciphertext back to readable HTML text
                        cleanText = CryptoEngine.decrypt(m.getContent(), masterKey);
                    } catch (Exception e) {
                        cleanText = "[Encrypted Content Unreadable]";
                    }
                    return new ChatMessageResponse(
                            m.getId(), cleanText, m.getSender().getName(), m.getSender().getId(), m.getTimestamp()
                    );
                })
                .toList();
    }
}