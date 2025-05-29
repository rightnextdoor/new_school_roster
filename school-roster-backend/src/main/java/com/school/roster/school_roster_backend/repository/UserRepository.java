package com.school.roster.school_roster_backend.repository;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRoles(Role role);
}
