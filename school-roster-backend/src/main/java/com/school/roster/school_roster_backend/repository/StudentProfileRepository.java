package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {
}
