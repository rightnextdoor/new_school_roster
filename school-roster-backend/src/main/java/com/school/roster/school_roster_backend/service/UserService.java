package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.controller.AuthController;
import com.school.roster.school_roster_backend.entity.*;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final NonStudentProfileRepository nonStudentProfileRepository;
    private final RosterRepository rosterRepository;
    private final GradeRepository gradeRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Random RANDOM = new SecureRandom();

    // === Generate Unique 12-Digit ID ===
    public String generateUniqueUserId() {
        String id;
        do {
            id = generateRandom12DigitNumber();
        } while (userRepository.existsById(id));
        return id;
    }

    private String generateRandom12DigitNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    // === Create New User ===
    public User createUser(User user) {
        user.setId(generateUniqueUserId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream()
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.name().equalsIgnoreCase("ADMIN")))
                .toList();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id)
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.name().equalsIgnoreCase("ADMIN")));
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .filter(user -> user.getRoles().stream()
                        .noneMatch(role -> role.name().equalsIgnoreCase("ADMIN")));
    }

    public User updateUser(String id, User updatedUserData) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        boolean isAdmin = existingUser.getRoles().stream()
                .anyMatch(role -> role.name().equalsIgnoreCase("ADMIN"));
        if (isAdmin) {
            throw new RuntimeException("Cannot update ADMIN user.");
        }

        existingUser.setEmail(updatedUserData.getEmail());

        if (updatedUserData.getPassword() != null && !updatedUserData.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUserData.getPassword()));
        }

        if (updatedUserData.getRoles() != null && !updatedUserData.getRoles().isEmpty()) {
            existingUser.setRoles(updatedUserData.getRoles());
        }

        return userRepository.save(existingUser);
    }

    public List<User> getUsersByRole(String roleName) {
        Role role = Role.valueOf(roleName);
        return userRepository.findAllByRoles(role);
    }

    public boolean hasRole(User user, String role) {
        return user.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase(role));
    }

    public boolean hasAnyRole(User user, String... roles) {
        for (String role : roles) {
            if (user.getRoles().stream().anyMatch(r -> r.name().equalsIgnoreCase(role))) {
                return true;
            }
        }
        return false;
    }

    public AuthController.UserInfoResponse updateRole(AuthController.RoleUpdate roleUpdate){
        User user = getUserById(roleUpdate.getUserId()).get();
        user.setRoles(roleUpdate.getRoles());
        AuthController.UserInfoResponse response = new AuthController.UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );
        return response;
    }

    // === Full Clean Delete User ===
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.name().equalsIgnoreCase("ADMIN"));
        if (isAdmin) {
            throw new RuntimeException("Cannot delete ADMIN user.");
        }

        // === 1. If Teacher ➔ Validate all assigned Rosters ===
        if (user.getRoles().contains(Role.TEACHER) || user.getRoles().contains(Role.TEACHER_LEAD)) {
            List<Roster> teacherRosters = rosterRepository.findByTeacherId(user.getId());
            for (Roster roster : teacherRosters) {
                if (roster.getTeacher() != null && roster.getTeacher().getId().equals(user.getId())) {
                    throw new RuntimeException("Cannot delete Teacher: Roster [" + roster.getSubjectName() + "] is still assigned. Reassign or delete the Roster first.");
                }
            }
        }

        // === 2. Delete linked Profile (Student or Non-Student) ===
        if (user.getStudentProfile() != null) {
            studentProfileRepository.delete(user.getStudentProfile());
        }
        if (user.getNonStudentProfile() != null) {
            nonStudentProfileRepository.delete(user.getNonStudentProfile());
        }

        // === 3. Unlink user from all Roster students lists ===
        List<Roster> allRosters = rosterRepository.findAll();
        for (Roster roster : allRosters) {
            roster.getStudents().remove(user);
        }

        // === 4. Delete all Grades where user is Student ===
        List<Grade> studentGrades = gradeRepository.findByStudentId(user.getId());
        gradeRepository.deleteAll(studentGrades);

        // === 5. Finally, Delete User ===
        userRepository.deleteById(id);
    }

}
