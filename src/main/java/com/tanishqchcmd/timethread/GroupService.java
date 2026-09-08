package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private StudentRepository studentRepository;

    public Group createGroup(String name, Student creator) {
        Group group = new Group();
        group.setName(name);
        group.setCreator(creator);
        Group savedGroup = groupRepository.save(group);

        List<Group> creatorGroups = new ArrayList<>(creator.getGroups());
        creatorGroups.add(savedGroup);
        creator.setGroups(creatorGroups);
        studentRepository.save(creator);

        return savedGroup;
    }

    public void inviteMember(Long groupId, String email, Student requestingStudent) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        if (group.getCreator() == null || !group.getCreator().getId().equals(requestingStudent.getId())) {
            throw new AccessDeniedException("Only the group creator can send invites");
        }

        Student invitee = studentRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No student found with that email"));

        boolean alreadyMember = invitee.getGroups().stream()
                .anyMatch(g -> g.getId().equals(group.getId()));
        if (alreadyMember) return;

        List<Group> inviteeGroups = new ArrayList<>(invitee.getGroups());
        inviteeGroups.add(group);
        invitee.setGroups(inviteeGroups);
        studentRepository.save(invitee);
    }
}