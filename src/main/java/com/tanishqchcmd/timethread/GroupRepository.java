package com.tanishqchcmd.timethread;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByStudentsId(Long studentId);
}