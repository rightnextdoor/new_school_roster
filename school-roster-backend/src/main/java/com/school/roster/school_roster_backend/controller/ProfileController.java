package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.service.ProfileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // === Create Student Profile ===
    @PostMapping("/student/create")
    public ResponseEntity<StudentProfile> createStudentProfile(@RequestBody CreateStudentProfileRequest request) {
        StudentProfile profile = profileService.createStudentProfile(request.getUserId(), request.getStudentProfile());
        return ResponseEntity.ok(profile);
    }

    // === Update Student Profile ===
    @PutMapping("/student/update")
    public ResponseEntity<StudentProfile> updateStudentProfile(@RequestBody UpdateStudentProfileRequest request) {
        StudentProfile profile = profileService.updateStudentProfile(request.getProfileId(), request.getUpdatedProfile());
        return ResponseEntity.ok(profile);
    }

    // === Delete Student Profile ===
    @DeleteMapping("/student/delete")
    public ResponseEntity<String> deleteStudentProfile(@RequestBody IdRequest request) {
        profileService.deleteStudentProfile(request.getId());
        return ResponseEntity.ok("Student profile deleted successfully.");
    }

    // === Create Non-Student Profile ===
    @PostMapping("/nonstudent/create")
    public ResponseEntity<NonStudentProfile> createNonStudentProfile(@RequestBody CreateNonStudentProfileRequest request) {
        NonStudentProfile profile = profileService.createNonStudentProfile(request.getUserId(), request.getNonStudentProfile());
        return ResponseEntity.ok(profile);
    }

    // === Update Non-Student Profile ===
    @PutMapping("/nonstudent/update")
    public ResponseEntity<NonStudentProfile> updateNonStudentProfile(@RequestBody UpdateNonStudentProfileRequest request) {
        NonStudentProfile profile = profileService.updateNonStudentProfile(request.getProfileId(), request.getUpdatedProfile());
        return ResponseEntity.ok(profile);
    }

    // === Delete Non-Student Profile ===
    @DeleteMapping("/nonstudent/delete")
    public ResponseEntity<String> deleteNonStudentProfile(@RequestBody IdRequest request) {
        profileService.deleteNonStudentProfile(request.getId());
        return ResponseEntity.ok("Non-student profile deleted successfully.");
    }

    // === GET APIs for ALL Profiles (Student or Non-Student) ===

    // --- Get profile by Profile ID ---
    @PostMapping("/getById")
    public ResponseEntity<Object> getProfileById(@RequestBody IdRequest request) {
        Object profile = profileService.getProfileById(request.getId());
        return ResponseEntity.ok(profile);
    }

    // --- Get profile by User ID ---
    @PostMapping("/getByUser")
    public ResponseEntity<Object> getProfileByUserId(@RequestBody UserIdRequest request) {
        Object profile = profileService.getProfileByUserId(request.getUserId());
        return ResponseEntity.ok(profile);
    }

    // --- Get All Profiles ---
    @GetMapping("/getAll")
    public ResponseEntity<List<Object>> getAllProfiles() {
        List<Object> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    // --- Get Own Profile (by logged-in User) ---
    @PostMapping("/getMyProfile")
    public ResponseEntity<Object> getMyProfile(@RequestBody UserIdRequest request) {
        Object profile = profileService.getProfileByUserId(request.getUserId());
        return ResponseEntity.ok(profile);
    }

    // === Request DTOs ===

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
