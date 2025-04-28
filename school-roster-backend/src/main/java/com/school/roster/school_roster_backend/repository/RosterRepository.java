package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RosterRepository extends JpaRepository<Roster, Long> {
    List<Roster> findByTeacherId(String teacherId);

    @Query("SELECT r FROM Roster r LEFT JOIN FETCH r.teacher LEFT JOIN FETCH r.students WHERE r.id = :id")
    Optional<Roster> findByIdWithTeacherAndStudents(@Param("id") Long id);
}
