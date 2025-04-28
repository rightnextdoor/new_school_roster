package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.ProfileService;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final UserService userService;
    private final RosterService rosterService;

    // === Create Student Profile ===
    @PostMapping("/student/create")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR', 'ADMIN')")
    public ResponseEntity<StudentProfile> createStudentProfile(@RequestBody CreateStudentProfileRequest request, Authentication authentication) {
        String loggedInEmail = authentication.getName();
        User loggedInUser = userService.getUserByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If TEACHER, verify they are linked to student via roster
        if (userService.hasRole(loggedInUser, "TEACHER") && !rosterService.isStudentUnderTeacher(loggedInUser.getId(), request.getUserId())) {
            throw new RuntimeException("Access denied: Student is not under your roster.");
        }

        StudentProfile profile = profileService.createStudentProfile(request.getUserId(), request.getStudentProfile());
        return ResponseEntity.ok(profile);
    }

    // === Update Student Profile ===
    @PutMapping("/student/update")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR', 'ADMIN')")
    public ResponseEntity<StudentProfile> updateStudentProfile(@RequestBody UpdateStudentProfileRequest request, Authentication authentication) {
        String loggedInEmail = authentication.getName();
        User loggedInUser = userService.getUserByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If TEACHER, verify control over student
        if (userService.hasRole(loggedInUser, "TEACHER") && !rosterService.isStudentUnderTeacher(loggedInUser.getId(), request.getUpdatedProfile().getLinkedUser().getId())) {
            throw new RuntimeException("Access denied: Student is not under your roster.");
        }

        StudentProfile profile = profileService.updateStudentProfile(request.getProfileId(), request.getUpdatedProfile());
        return ResponseEntity.ok(profile);
    }

    // === Delete Student Profile ===
    @DeleteMapping("/student/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<String> deleteStudentProfile(@RequestBody IdRequest request) {
        profileService.deleteStudentProfile(request.getId());
        return ResponseEntity.ok("Student profile deleted successfully.");
    }

    // === Create Non-Student Profile ===
    @PostMapping("/nonstudent/create")
    @PreAuthorize("isAuthenticated()") // 🔥 only basic check; real validation inside
    public ResponseEntity<NonStudentProfile> createNonStudentProfile(
            Authentication authentication,
            @RequestBody CreateNonStudentProfileRequest request) {

        User currentUser = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isAdmin = userService.hasAnyRole(currentUser, "ADMIN", "ADMINISTRATOR", "OFFICE_ADMINISTRATOR");

        if (!isAdmin) {
            // ⚡ Regular user
            if (!currentUser.getId().equals(request.getUserId())) {
                throw new RuntimeException("You can only create your own non-student profile.");
            }

            if (userService.hasRole(currentUser, "STUDENT")) {
                throw new RuntimeException("Students are not allowed to create non-student profiles.");
            }
        }

        NonStudentProfile profile = profileService.createNonStudentProfile(
                request.getUserId(),
                request.getNonStudentProfile()
        );
        return ResponseEntity.ok(profile);
    }


    // === Update Non-Student Profile ===
    @PutMapping("/nonstudent/update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NonStudentProfile> updateNonStudentProfile(@RequestBody UpdateNonStudentProfileRequest request, Authentication authentication) {
        String loggedInEmail = authentication.getName();
        User loggedInUser = userService.getUserByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!userService.hasAnyRole(loggedInUser, "ADMIN", "ADMINISTRATOR") &&
                !request.getUpdatedProfile().getLinkedUser().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("Access denied: You can only update your own non-student profile.");
        }

        NonStudentProfile profile = profileService.updateNonStudentProfile(request.getProfileId(), request.getUpdatedProfile());
        return ResponseEntity.ok(profile);
    }

    // === Delete Non-Student Profile ===
    @DeleteMapping("/nonstudent/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<String> deleteNonStudentProfile(@RequestBody IdRequest request) {
        profileService.deleteNonStudentProfile(request.getId());
        return ResponseEntity.ok("Non-student profile deleted successfully.");
    }

    // === Get Profile by ID ===
    @PostMapping("/student/getById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<StudentProfile> getStudentProfileById(@RequestBody IdRequest request) {
        StudentProfile profile = profileService.getStudentProfileById(request.getId());
        return ResponseEntity.ok(profile);
    }

    @PostMapping("/nonstudent/getById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<NonStudentProfile> getNonStudentProfileById(@RequestBody IdRequest request) {
        NonStudentProfile profile = profileService.getNonStudentProfileById(request.getId());
        return ResponseEntity.ok(profile);
    }


    // === Get Profile by User ID ===
    @PostMapping("/getByUser")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getProfileByUserId(@RequestBody UserIdRequest request) {
        Object profile = profileService.getProfileByUserId(request.getUserId());
        return ResponseEntity.ok(profile);
    }

    // === Get All Profiles ===
    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<List<Object>> getAllProfiles() {
        List<Object> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    // === Get My Profile ===
    @GetMapping("/getMyProfile")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getMyProfile(Authentication authentication) {
        String loggedInEmail = authentication.getName();
        User user = userService.getUserByEmail(loggedInEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Object profile = profileService.getProfileByUserId(user.getId());
        return ResponseEntity.ok(profile);
    }

    // === DTOs ===
    @Data
    @AllArgsConstructor
    private static class IdRequest {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    private static class UserIdRequest {
        private String userId;
    }

    @Data
    @AllArgsConstructor
    private static class CreateStudentProfileRequest {
        private String userId;
        private StudentProfile studentProfile;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateStudentProfileRequest {
        private Long profileId;
        private StudentProfile updatedProfile;
    }

    @Data
    @AllArgsConstructor
    private static class CreateNonStudentProfileRequest {
        private String userId;
        private NonStudentProfile nonStudentProfile;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateNonStudentProfileRequest {
        private Long profileId;
        private NonStudentProfile updatedProfile;
    }
}
