package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email); // New! Helps during create to prevent duplicate email
}
