package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/add")
    public String addTask(
            Authentication authentication,
            @RequestParam String title,
            @RequestParam LocalTime startTime,
            @RequestParam LocalTime endTime,
            @RequestParam(required = false) boolean recurring,
            @RequestParam(required = false) LocalDate specificDate,
            @RequestParam(required = false) List<String> daysOfWeek) {

        Student owner = studentRepository.findByEmail(authentication.getName()).orElseThrow();

        Task task = new Task();
        task.setOwner(owner);
        task.setTitle(title);
        task.setStartTime(startTime);
        task.setEndTime(endTime);
        task.setRecurring(recurring);

        // ... (inside your addTask method)
        if (recurring && daysOfWeek != null) {
            // Join the list into a comma-separated string like "1,3,5"
            task.setDaysOfWeek(String.join(",", daysOfWeek));
        } else {
            task.setSpecificDate(specificDate);
        }

        taskRepository.save(task);
        return "redirect:/dashboard?clear=true";
    } // <--- THIS BRACE WAS MISSING! It closes the addTask method.

    // NOW we can safely start the new method:
    @PostMapping("/delete/{id}")
    public String deleteTask(Authentication authentication, @org.springframework.web.bind.annotation.PathVariable Long id) {
        Student owner = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();

        // Security check: Only delete if the logged-in user actually owns this task
        if (task.getOwner().getId().equals(owner.getId())) {
            taskRepository.delete(task);
        }
        return "redirect:/dashboard?clear=true";
    }

} // <--- This final brace closes the entire TaskController class.