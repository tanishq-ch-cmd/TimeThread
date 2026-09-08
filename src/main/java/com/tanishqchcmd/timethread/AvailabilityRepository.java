package com.tanishqchcmd.timethread;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

    List<Availability> findByStudent(Student student);

    // We changed this to accept the whole Student object instead of just the ID
    List<Availability> findByStudentAndDate(Student student, LocalDate date);
}