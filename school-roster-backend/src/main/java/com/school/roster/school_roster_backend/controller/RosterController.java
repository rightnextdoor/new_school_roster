package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rosters")
@RequiredArgsConstructor
public class RosterController {

    private final RosterService rosterService;
    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<Roster> createRoster(@RequestBody Roster roster, Authentication authentication) {
        String userEmail = authentication.getName();
        User teacher = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found."));

        Roster created = rosterService.createRoster(roster, teacher.getId());
        return ResponseEntity.ok(created);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<Roster> updateRoster(@RequestBody UpdateRosterRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!rosterService.canEditRoster(request.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update this roster.");
        }

        Roster updated = rosterService.updateRoster(request.getRosterId(), request.getRosterData());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<String> deleteRoster(@RequestBody IdRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!rosterService.canEditRoster(request.getId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to delete this roster.");
        }

        rosterService.deleteRoster(request.getId());
        return ResponseEntity.ok("Roster deleted successfully.");
    }

    @PostMapping("/addStudent")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<Roster> addStudentToRoster(@RequestBody AddStudentRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!rosterService.canEditRoster(request.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to add students to this roster.");
        }

        Roster updated = rosterService.addStudentToRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/removeStudent")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<Roster> removeStudentFromRoster(@RequestBody AddStudentRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!rosterService.canEditRoster(request.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to remove students from this roster.");
        }

        Roster updated = rosterService.removeStudentFromRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/reassignTeacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<Roster> reassignTeacher(@RequestBody ReassignTeacherRequest request) {
        Roster updatedRoster = rosterService.reassignTeacher(request.getRosterId(), request.getNewTeacherId());
        return ResponseEntity.ok(updatedRoster);
    }

    @PostMapping("/getById")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RosterResponse> getRosterById(@RequestBody IdRequest request, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));

        if (!rosterService.canViewRoster(request.getId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to view this roster.");
        }

        Roster roster = rosterService.getRosterById(request.getId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }


    @PostMapping("/getByStudent")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<RosterResponse>> getRostersByStudent(Authentication authentication) {
        String userEmail = authentication.getName();
        User student = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Student not found."));

        List<Roster> rosters = rosterService.getRostersByStudentId(student.getId());
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }


    @PostMapping("/getByTeacher")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<List<RosterResponse>> getRostersByTeacher(Authentication authentication) {
        String userEmail = authentication.getName();
        User teacher = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Teacher not found."));

        List<Roster> rosters = rosterService.getRostersByTeacherId(teacher.getId());
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }


    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<List<RosterResponse>> getAllRosters() {
        List<Roster> rosters = rosterService.getAllRosters();
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }

    // === Build Response ===
    public RosterResponse buildRosterResponse(Roster roster) {
        return new RosterResponse(
                roster.getId(),
                roster.getSubjectName(),
                roster.getPeriod(),
                roster.getNickname(),
                roster.getTeacher() != null && roster.getTeacher().getNonStudentProfile() != null ? roster.getTeacher().getNonStudentProfile().getFirstName() : null,
                roster.getTeacher() != null && roster.getTeacher().getNonStudentProfile() != null ? roster.getTeacher().getNonStudentProfile().getLastName() : null,
                roster.getStudents().stream().map(student -> {
                    Grade grade = roster.getGrades().stream()
                            .filter(g -> g.getStudent() != null && g.getStudent().getId().equals(student.getId()))
                            .findFirst()
                            .orElse(null);
                    return new StudentInfo(
                            student.getId(),
                            student.getStudentProfile() != null ? student.getStudentProfile().getFirstName() : null,
                            student.getStudentProfile() != null ? student.getStudentProfile().getLastName() : null,
                            grade != null ? grade.getInitialGrade() : null,
                            grade != null && grade.getFinalStatus() != null ? grade.getFinalStatus().name() : null // 🛠 add this
                    );
                }).collect(Collectors.toList()),
                roster.getClassGpa()
        );
    }


    // === DTOs ===

    @Data
    @AllArgsConstructor
    public static class UpdateRosterRequest {
        private Long rosterId;
        private Roster updatedRoster;

        public Roster getRosterData() {
            return updatedRoster;
        }
    }

    @Data
    @AllArgsConstructor
    public static class ReassignTeacherRequest {
        private Long rosterId;
        private String newTeacherId;
    }

    @Data
    @AllArgsConstructor
    public static class RosterResponse {
        private Long rosterId;
        private String subjectName;
        private String period;
        private String nickname;
        private String teacherFirstName;
        private String teacherLastName;
        private List<StudentInfo> students;
        private Float classGpa;
    }

    @Data
    @AllArgsConstructor
    public static class StudentInfo {
        private String studentId;
        private String firstName;
        private String lastName;
        private Double finalGpa;
        private String finalStatus;
    }

    @Data
    @AllArgsConstructor
    public static class IdRequest {
        private Long id;
    }

    @Data
    @AllArgsConstructor
    public static class AddStudentRequest {
        private Long rosterId;
        private String studentId;
    }
}
