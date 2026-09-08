package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private GroupRepository groupRepository;

    @PostMapping
    public String createGroup(@RequestParam String name, Authentication authentication) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Group group = groupService.createGroup(name, student);
        return "redirect:/dashboard?groupId=" + group.getId();
    }

    @PostMapping("/{groupId}/invite")
    public String invite(@PathVariable Long groupId, @RequestParam String email,
                         Authentication authentication, RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        try {
            groupService.inviteMember(groupId, email, student);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("inviteError", e.getMessage());
        }
        return "redirect:/dashboard?groupId=" + groupId;
    }

    @PostMapping("/{groupId}/rename")
    public String renameGroup(@PathVariable Long groupId, @RequestParam String newName, Authentication authentication) {
        Student currentStudent = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();

        // Allow if creator is null (legacy group) or if current user is the creator
        if (group.getCreator() == null || group.getCreator().getId().equals(currentStudent.getId())) {
            group.setName(newName);
            groupRepository.save(group);
        }
        return "redirect:/dashboard?groupId=" + groupId;
    }

    @PostMapping("/{groupId}/kick/{memberId}")
    public String kickMember(@PathVariable Long groupId, @PathVariable Long memberId, Authentication authentication) {
        Student currentStudent = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();

        if ((group.getCreator() == null || group.getCreator().getId().equals(currentStudent.getId())) && !currentStudent.getId().equals(memberId)) {
            Student memberToKick = studentRepository.findById(memberId).orElseThrow();
            group.getStudents().remove(memberToKick);
            memberToKick.getGroups().remove(group);

            groupRepository.save(group);
            studentRepository.save(memberToKick);
        }
        return "redirect:/dashboard?groupId=" + groupId;
    }

    @PostMapping("/{groupId}/delete")
    public String deleteGroup(@PathVariable Long groupId, Authentication authentication) {
        Student currentStudent = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Group group = groupRepository.findById(groupId).orElseThrow();

        // Allow if creator is null (legacy group) or if current user is the creator
        if (group.getCreator() == null || group.getCreator().getId().equals(currentStudent.getId())) {
            for (Student student : group.getStudents()) {
                student.getGroups().remove(group);
                studentRepository.save(student);
            }
            groupRepository.delete(group);
        }
        return "redirect:/dashboard?clear=true";
    }
}
