package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NonStudentProfileRepository extends JpaRepository<NonStudentProfile, Long> {
}
