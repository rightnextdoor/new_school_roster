package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.Roster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RosterRepository extends JpaRepository<Roster, Long> {
    List<Roster> findByTeacherId(String teacherId);
}
