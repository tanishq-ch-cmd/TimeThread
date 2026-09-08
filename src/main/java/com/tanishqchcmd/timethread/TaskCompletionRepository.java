package com.tanishqchcmd.timethread;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface TaskCompletionRepository extends JpaRepository<TaskCompletion, Long> {
    Optional<TaskCompletion> findByTaskIdAndCompletedDate(Long taskId, LocalDate completedDate);
}