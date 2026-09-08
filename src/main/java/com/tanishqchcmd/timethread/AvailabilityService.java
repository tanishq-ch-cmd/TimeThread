package com.tanishqchcmd.timethread;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class AvailabilityService {

    @Autowired
    private AvailabilityRepository availabilityRepository;

    public void addAvailability(Student student, LocalDate date, LocalTime startTime, LocalTime endTime) {
        Availability availability = new Availability();
        availability.setStudent(student);
        availability.setDate(date);
        availability.setStartTime(startTime);
        availability.setEndTime(endTime);
        availabilityRepository.save(availability);
    }

    public void deleteAvailability(Long id, Student student) {
        Availability availability = availabilityRepository.findById(id).orElseThrow();
        if (availability.getStudent().getId().equals(student.getId())) {
            availabilityRepository.delete(availability);
        }
    }

    public List<Availability> findByStudent(Student student) {
        return availabilityRepository.findByStudent(student);
    }
}