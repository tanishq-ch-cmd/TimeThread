package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.crypto.SecretKey;
import java.util.List;

@Controller
public class UIController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private DirectMessageRepository dmRepository;
    @Autowired
    private RoutineRepository routineRepository;
    @Autowired
    private GroupRepository groupRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model,
                            @RequestParam(required = false) Long groupId,
                            @RequestParam(required = false) Long dmUserId,
                            @RequestParam(required = false) Boolean clear) {

        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Group> groups = groupRepository.findByStudentsId(student.getId());
        model.addAttribute("groups", groups);

        Group selectedGroup = null;
        Student chatUser = null;

        if (!Boolean.TRUE.equals(clear) && groupId != null) {
            selectedGroup = groups.stream().filter(g -> g.getId().equals(groupId)).findFirst().orElse(null);
        } else if (!Boolean.TRUE.equals(clear) && dmUserId != null) {
            chatUser = studentRepository.findById(dmUserId).orElse(null);
        }

        model.addAttribute("student", student);
        model.addAttribute("groups", groups);
        model.addAttribute("selectedGroup", selectedGroup);
        model.addAttribute("chatUser", chatUser);

        // --- DECRYPTION PIPELINE ---
        SecretKey masterKey = CryptoEngine.getMasterKey();

        // 1. Fetch and Decrypt Group Chat Messages
        List<Message> groupMessages = selectedGroup != null
                ? messageRepository.findByGroupIdOrderByTimestampAsc(selectedGroup.getId())
                : List.of();

        groupMessages.forEach(m -> {
            try {
                m.setContent(CryptoEngine.decrypt(m.getContent(), masterKey));
            } catch (Exception e) {
                // If it fails (e.g., an old plaintext message from before the encryption update), leave it as is
            }
        });
        model.addAttribute("messages", groupMessages);

        // 2. Fetch and Decrypt Private Direct Messages
        List<DirectMessage> directMessages = chatUser != null
                ? dmRepository.findConversation(student, chatUser)
                : List.of();

        directMessages.forEach(dm -> {
            try {
                dm.setContent(CryptoEngine.decrypt(dm.getContent(), masterKey));
            } catch (Exception e) {
                // Ignore exception for legacy plaintext messages
            }
        });
        model.addAttribute("dmMessages", directMessages);

        // Schedule overlapping algorithm
        model.addAttribute("optimalSlots",
                selectedGroup != null
                        ? schedulerService.findOptimalSlots(selectedGroup.getId())
                        : List.of());
        model.addAttribute("routines", routineRepository.findByOwner(student));

        return "dashboard";
    }
}