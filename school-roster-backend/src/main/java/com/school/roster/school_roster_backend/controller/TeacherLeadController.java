package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.TeacherLeadService;
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
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class TeacherLeadController {

    private final TeacherLeadService teacherLeadService;
    private final UserService userService;

    @PostMapping("/addTeacher")
    @PreAuthorize("hasAnyRole('TEACHER_LEAD', 'ADMIN', 'ADMINISTRATOR')")
    public ResponseEntity<String> addTeacher(@RequestBody ManageTeacherRequest request, Authentication authentication) {
        User caller = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
        teacherLeadService.addTeacher(request.getLeadId(), request.getTeacherId(), caller);
        return ResponseEntity.ok("Teacher added to lead.");
    }

    @PostMapping("/removeTeacher")
    @PreAuthorize("hasAnyRole('TEACHER_LEAD', 'ADMIN', 'ADMINISTRATOR')")
    public ResponseEntity<String> removeTeacher(@RequestBody ManageTeacherRequest request, Authentication authentication) {
        User caller = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
        teacherLeadService.removeTeacher(request.getLeadId(), request.getTeacherId(), caller);
        return ResponseEntity.ok("Teacher removed from lead.");
    }

    @GetMapping("/getMyTeachers")
    @PreAuthorize("hasRole('TEACHER_LEAD')")
    public ResponseEntity<List<User>> getMyTeachers(Authentication authentication) {
        User caller = userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
        List<User> teachers = teacherLeadService.getMyTeachers(caller);
        return ResponseEntity.ok(teachers);
    }

    @PostMapping("/getTeachersByLeadId")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR')")
    public ResponseEntity<List<User>> getTeachersForAdmin(@RequestBody LeadIdRequest request) {
        List<User> teachers = teacherLeadService.getTeachersForAdmin(request.getLeadId());
        return ResponseEntity.ok(teachers);
    }

    @Data
    @AllArgsConstructor
    public static class ManageTeacherRequest {
        private String leadId;
        private String teacherId;
    }

    @Data
    @AllArgsConstructor
    public static class LeadIdRequest {
        private String leadId;
    }
}
