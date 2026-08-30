package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class UIController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private MessageRepository messageRepository;

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
                            @RequestParam(required = false) Long groupId) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Group> groups = student.getGroups();

        Group selectedGroup = null;
        if (groupId != null) {
            selectedGroup = groups.stream().filter(g -> g.getId().equals(groupId)).findFirst().orElse(null);
        }
        if (selectedGroup == null && !groups.isEmpty()) {
            selectedGroup = groups.get(0);
        }

        model.addAttribute("student", student);
        model.addAttribute("groups", groups);
        model.addAttribute("selectedGroup", selectedGroup);
        model.addAttribute("messages",
                selectedGroup != null
                        ? messageRepository.findByGroupIdOrderByTimestampAsc(selectedGroup.getId())
                        : List.of());

        return "dashboard";
    }
}