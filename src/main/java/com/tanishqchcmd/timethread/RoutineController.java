package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/routines")
public class RoutineController {

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping("/add")
    public String addRoutine(@RequestParam String name, @RequestParam String category, Authentication authentication) {
        Student owner = studentRepository.findByEmail(authentication.getName()).orElseThrow();

        Routine routine = new Routine();
        routine.setOwner(owner);
        routine.setName(name);
        routine.setCategory(category);
        routine.setCurrentStreak(0);
        routine.setLongestStreak(0);

        routineRepository.save(routine);
        // --- NEW XP LOGIC ---
        // Award 20 XP for a successful habit check-in.
        owner.addXp(20);
        studentRepository.save(owner);
        return "redirect:/dashboard?clear=true";
    }

    @PostMapping("/{id}/checkin")
    public String checkIn(@PathVariable Long id, Authentication authentication) {
        Student owner = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Routine routine = routineRepository.findById(id).orElseThrow();

        // Security: Make sure they own this routine
        if (routine.getOwner().getId().equals(owner.getId())) {
            LocalDate today = LocalDate.now();
            LocalDate last = routine.getLastCompletedDate();

            if (last == null || ChronoUnit.DAYS.between(last, today) > 1) {
                // First time checking in, or they missed a day
                routine.setCurrentStreak(1);
            } else if (ChronoUnit.DAYS.between(last, today) == 1) {
                // Perfect consecutive check-in
                routine.setCurrentStreak(routine.getCurrentStreak() + 1);
            }
            // (If the difference is 0, they already checked in today, so we do nothing)

            // Update Longest Streak high score
            if (routine.getCurrentStreak() > routine.getLongestStreak()) {
                routine.setLongestStreak(routine.getCurrentStreak());
            }

            routine.setLastCompletedDate(today);
            routineRepository.save(routine);
        }
        return "redirect:/dashboard?clear=true";
    }

    @PostMapping("/{id}/delete")
    public String deleteRoutine(@PathVariable Long id, Authentication authentication) {
        Student owner = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        Routine routine = routineRepository.findById(id).orElseThrow();

        if (routine.getOwner().getId().equals(owner.getId())) {
            routineRepository.delete(routine);
        }
        return "redirect:/dashboard?clear=true";
    }
}