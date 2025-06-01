package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.GradeService;
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
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;
    private final RosterService rosterService;
    private final UserService userService;

    // === Update Grade ===
    @PutMapping("/update")
    public ResponseEntity<GradeResponse> updateGrade(@RequestBody UpdateGradeRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!rosterService.canEditRoster(request.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update grades in this roster.");
        }

        Grade updatedGrade = gradeService.updateGrade(
                request.getGradeId(),
                request.getPerformanceScores(),
                request.getQuizScores(),
                request.getQuarterlyExamScores()
        );
        return ResponseEntity.ok(buildGradeResponse(updatedGrade));
    }

    // === Delete Grade ===
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGrade(@RequestBody GradeIdRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!gradeService.canDeleteGrade(request.getGradeId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to delete this grade.");
        }

        gradeService.deleteGrade(request.getGradeId());
        return ResponseEntity.ok("Grade deleted successfully.");
    }

    // === Get Grade by ID ===
    @PostMapping("/getById")
    public ResponseEntity<GradeResponse> getGradeById(@RequestBody GradeIdRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!gradeService.canViewGrade(request.getGradeId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to view this grade.");
        }

        Grade grade = gradeService.getGradeById(request.getGradeId());
        return ResponseEntity.ok(buildGradeResponse(grade));
    }

    // === Get Grades by Roster ===
    @PostMapping("/getByRoster")
    public ResponseEntity<List<GradeResponse>> getGradesByRoster(@RequestBody RosterIdRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!rosterService.canViewRoster(request.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to view grades in this roster.");
        }

        List<Grade> grades = gradeService.getGradesByRosterId(request.getRosterId());
        return ResponseEntity.ok(grades.stream().map(this::buildGradeResponse).collect(Collectors.toList()));
    }

    // === Get Grades by Student ===
    @PostMapping("/getByStudent")
    public ResponseEntity<List<GradeResponse>> getGradesByStudent(@RequestBody StudentIdRequest request, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        if (!currentUser.getId().equals(request.getStudentId()) && !userService.hasAnyRole(currentUser, "ADMIN", "ADMINISTRATOR", "TEACHER", "TEACHER_LEAD")) {
            throw new RuntimeException("Access denied: You are not allowed to view grades of this student.");
        }

        List<Grade> grades = gradeService.getGradesByStudentId(request.getStudentId());
        return ResponseEntity.ok(grades.stream().map(this::buildGradeResponse).collect(Collectors.toList()));
    }

    // === Get My GPA ===
    @GetMapping("/myGpa")
    public ResponseEntity<GradeService.StudentGpaResponse> getMyGpa(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);

        GradeService.StudentGpaResponse gpaSummary = gradeService.getMyGpa(currentUser.getId());
        return ResponseEntity.ok(gpaSummary);
    }

    // === Helper Methods ===
    private User getCurrentUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    private GradeResponse buildGradeResponse(Grade grade) {
        var details = grade.getScoreDetails(); // embedded ScoreDetails

        return new GradeResponse(
                // ─── core grade identifiers ───────────────────────────────────────────
                grade.getId(),
                grade.getInitialGrade(),
                grade.getFinalStatus() != null ? grade.getFinalStatus().name() : null,

                // ─── student summary ───────────────────────────────────────────────
                grade.getStudent().getId(),
                grade.getStudent().getStudentProfile() != null
                        ? grade.getStudent().getStudentProfile().getFirstName() : null,
                grade.getStudent().getStudentProfile() != null
                        ? grade.getStudent().getStudentProfile().getLastName() : null,

                // ─── roster summary ────────────────────────────────────────────────
                grade.getRoster().getId(),
                grade.getRoster().getSubjectName(),
                grade.getRoster().getPeriod(),
                grade.getRoster().getNickname(),
                grade.getRoster().getTeacher() != null
                        && grade.getRoster().getTeacher().getNonStudentProfile() != null
                        ? grade.getRoster().getTeacher().getNonStudentProfile().getFirstName()
                        : null,
                grade.getRoster().getTeacher() != null
                        && grade.getRoster().getTeacher().getNonStudentProfile() != null
                        ? grade.getRoster().getTeacher().getNonStudentProfile().getLastName()
                        : null,

                // ─── PERFORMANCE breakdown ────────────────────────────────────────
                details.getPerformanceScores(),
                details.getPerformanceTotal(),
                details.getPerformancePs(),
                details.getPerformanceWs(),

                // ─── QUIZ breakdown ───────────────────────────────────────────────
                details.getQuizScores(),
                details.getQuizTotal(),
                details.getQuizPs(),
                details.getQuizWs(),

                // ─── EXAM breakdown ───────────────────────────────────────────────
                details.getQuarterlyExamScores(),
                details.getQuarterlyExamTotal(),
                details.getQuarterlyExamPs(),
                details.getQuarterlyExamWs()
        );
    }


    // === DTOs ===
    @Data
    @AllArgsConstructor
    public static class UpdateGradeRequest {
        private Long rosterId;
        private Long gradeId;
        private List<Integer> performanceScores;
        private List<Integer> quizScores;
        private List<Integer> quarterlyExamScores;
    }

    @Data
    @AllArgsConstructor
    public static class GradeIdRequest {
        private Long gradeId;
    }

    @Data
    @AllArgsConstructor
    public static class RosterIdRequest {
        private Long rosterId;
    }

    @Data
    @AllArgsConstructor
    public static class StudentIdRequest {
        private String studentId;
    }

    @Data
    @AllArgsConstructor
    public static class GradeResponse {
        // ─── Basic identifiers ─────────────────────────────────────────────
        private Long gradeId;
        private Double initialGrade;
        private String finalStatus;

        // ─── Student summary ──────────────────────────────────────────────
        private String studentId;
        private String studentFirstName;
        private String studentLastName;

        // ─── Roster summary ───────────────────────────────────────────────
        private Long rosterId;
        private String subjectName;
        private String period;
        private String nickname;
        private String teacherFirstName;
        private String teacherLastName;

        // ─── PERFORMANCE breakdown ────────────────────────────────────────
        private List<Integer> performanceScores;
        private Integer performanceTotal;
        private Double performancePs;
        private Double performanceWs;

        // ─── QUIZ breakdown ───────────────────────────────────────────────
        private List<Integer> quizScores;
        private Integer quizTotal;
        private Double quizPs;
        private Double quizWs;

        // ─── EXAM breakdown ───────────────────────────────────────────────
        private List<Integer> quarterlyExamScores;
        private Integer quarterlyExamTotal;
        private Double quarterlyExamPs;
        private Double quarterlyExamWs;
    }
}
