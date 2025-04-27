package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(String studentId);
    List<Grade> findByRosterId(Long rosterId);
}
