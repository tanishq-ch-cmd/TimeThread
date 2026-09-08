package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CalendarController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/calendar/events")
    public List<Map<String, Object>> getEvents(Authentication authentication) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        List<Task> tasks = taskRepository.findByOwner(student);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Task task : tasks) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", task.getId().toString());
            event.put("title", task.getTitle());
            event.put("color", "#212529"); // Matches our dark UI theme

            if (task.isRecurring()) {
                // FullCalendar configuration for recurring events
                event.put("startTime", task.getStartTime().toString());
                event.put("endTime", task.getEndTime().toString());
                event.put("daysOfWeek", List.of(task.getDaysOfWeek().split(",")));

                if (task.getRecurStart() != null) event.put("startRecur", task.getRecurStart().toString());
                if (task.getRecurEnd() != null) event.put("endRecur", task.getRecurEnd().toString());
            } else {
                // Configuration for single-day events
                event.put("start", task.getSpecificDate() + "T" + task.getStartTime());
                event.put("end", task.getSpecificDate() + "T" + task.getEndTime());
            }
            events.add(event);
        }
        return events;
    }
}