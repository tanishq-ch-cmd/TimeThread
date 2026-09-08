package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public List<Map<String, Object>> getCalendarEvents(Long studentId) {
        List<Map<String, Object>> events = new ArrayList<>();

        for (Task task : taskRepository.findByOwnerId(studentId)) {
            Map<String, Object> event = new HashMap<>();
            event.put("id", task.getId());
            event.put("title", task.getTitle());

            if (task.isRecurring()) {
                List<Integer> days = new ArrayList<>();
                for (String d : task.getDaysOfWeek().split(",")) {
                    days.add(Integer.parseInt(d.trim()));
                }
                event.put("daysOfWeek", days);
                event.put("startTime", task.getStartTime().toString());
                event.put("endTime", task.getEndTime().toString());
                if (task.getRecurStart() != null) event.put("startRecur", task.getRecurStart().toString());
                if (task.getRecurEnd() != null) event.put("endRecur", task.getRecurEnd().toString());
            } else {
                event.put("start", task.getSpecificDate() + "T" + task.getStartTime());
                event.put("end", task.getSpecificDate() + "T" + task.getEndTime());
            }

            events.add(event);
        }

        return events;
    }
}