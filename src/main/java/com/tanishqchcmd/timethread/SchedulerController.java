package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groups")
public class SchedulerController {

    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AvailabilityRepository availabilityRepository;

    @GetMapping("/{groupId}/optimal-times")
    public List<OptimalSlot> getOptimalTimes(@PathVariable Long groupId) {
        return schedulerService.findOptimalSlots(groupId);
    }

    @GetMapping("/{groupId}/debug")
    public Map<String, Object> debugGroup(@PathVariable Long groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow();
        List<Map<String, Object>> members = group.getStudents().stream()
                .map(s -> {
                    // FIX: Removed the extra ) and added .stream() before .map()
                    List<Map<String, Object>> blockDetails = availabilityRepository.findByStudent(s).stream()
                            .map(a -> Map.<String, Object>of(
                                    "day", a.getDate(),
                                    "start", a.getStartTime(),
                                    "end", a.getEndTime()
                            ))
                            .toList();
                    return Map.<String, Object>of("id", s.getId(), "email", s.getEmail(), "blocks", blockDetails);
                })
                .toList();
        return Map.of("groupSize", group.getStudents().size(), "members", members);
    }
}