package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/availability")
public class AvailabilityController {

    @Autowired
    private AvailabilityService availabilityService;
    @Autowired
    private StudentRepository studentRepository;

    @GetMapping
    public String viewAvailability(Authentication authentication, Model model) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("availabilities", availabilityService.findByStudent(student));
        return "availability";
    }

    @PostMapping
    public String addAvailability(@RequestParam LocalDate date,
                                  @RequestParam LocalTime startTime,
                                  @RequestParam LocalTime endTime,
                                  Authentication authentication) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        availabilityService.addAvailability(student, date, startTime, endTime);
        return "redirect:/availability";
    }

    @PostMapping("/delete/{id}")
    public String deleteAvailability(@PathVariable Long id, Authentication authentication) {
        Student student = studentRepository.findByEmail(authentication.getName()).orElseThrow();
        availabilityService.deleteAvailability(id, student);
        return "redirect:/availability";
    }
}