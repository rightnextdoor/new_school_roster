package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.service.GradeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // === Update Grade ===
    @PutMapping("/update")
    public ResponseEntity<GradeResponse> updateGrade(@RequestBody UpdateGradeRequest request) {
        Grade updatedGrade = gradeService.updateGrades(
                request.getStudentId(),
                request.getRosterId(),
                request.getPerformanceScores(),
                request.getQuizScores(),
                request.getQuarterlyExamScores()
        );
        return ResponseEntity.ok(buildGradeResponse(updatedGrade));
    }

    // === Delete Grade ===
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGrade(@RequestBody GradeIdRequest request) {
        gradeService.deleteGrade(request.getGradeId());
        return ResponseEntity.ok("Grade deleted successfully.");
    }

    // === Get Grade by ID ===
    @PostMapping("/getById")
    public ResponseEntity<GradeResponse> getGradeById(@RequestBody GradeIdRequest request) {
        Grade grade = gradeService.getGradeById(request.getGradeId());
        return ResponseEntity.ok(buildGradeResponse(grade));
    }

    // === Get Grades by Roster ===
    @PostMapping("/getByRoster")
    public ResponseEntity<List<GradeResponse>> getGradesByRoster(@RequestBody RosterIdRequest request) {
        List<Grade> grades = gradeService.getGradesByRosterId(request.getRosterId());
        return ResponseEntity.ok(grades.stream().map(this::buildGradeResponse).collect(Collectors.toList()));
    }

    // === Get Grades by Student ===
    @PostMapping("/getByStudent")
    public ResponseEntity<List<GradeResponse>> getGradesByStudent(@RequestBody StudentIdRequest request) {
        List<Grade> grades = gradeService.getGradesByStudentId(request.getStudentId());
        return ResponseEntity.ok(grades.stream().map(this::buildGradeResponse).collect(Collectors.toList()));
    }

    // === Build a clean readable response ===
    private GradeResponse buildGradeResponse(Grade grade) {
        return new GradeResponse(
                grade.getId(),
                grade.getFinalGpa(),
                grade.getStudent() != null ? grade.getStudent().getId() : null,
                grade.getStudent() != null && grade.getStudent().getStudentProfile() != null ? grade.getStudent().getStudentProfile().getFirstName() : null,
                grade.getStudent() != null && grade.getStudent().getStudentProfile() != null ? grade.getStudent().getStudentProfile().getLastName() : null,
                grade.getRoster() != null ? grade.getRoster().getId() : null,
                grade.getRoster() != null ? grade.getRoster().getSubjectName() : null,
                grade.getRoster() != null ? grade.getRoster().getPeriod() : null,
                grade.getRoster() != null ? grade.getRoster().getNickname() : null,
                grade.getRoster() != null && grade.getRoster().getTeacher() != null && grade.getRoster().getTeacher().getNonStudentProfile() != null
                        ? grade.getRoster().getTeacher().getNonStudentProfile().getFirstName() : null,
                grade.getRoster() != null && grade.getRoster().getTeacher() != null && grade.getRoster().getTeacher().getNonStudentProfile() != null
                        ? grade.getRoster().getTeacher().getNonStudentProfile().getLastName() : null
        );
    }

    // === DTOs ===

    @Data
    @AllArgsConstructor
    private static class UpdateGradeRequest {
        private String studentId;
        private Long rosterId;
        private List<Float> performanceScores;
        private List<Float> quizScores;
        private List<Float> quarterlyExamScores;
    }

    @Data
    @AllArgsConstructor
    private static class GradeIdRequest {
        private Long gradeId;
    }

    @Data
    @AllArgsConstructor
    private static class RosterIdRequest {
        private Long rosterId;
    }

    @Data
    @AllArgsConstructor
    private static class StudentIdRequest {
        private String studentId;
    }

    @Data
    @AllArgsConstructor
    private static class GradeResponse {
        private Long gradeId;
        private Float finalGpa;

        private String studentId;
        private String studentFirstName;
        private String studentLastName;

        private Long rosterId;
        private String subjectName;
        private String period;
        private String nickname;
        private String teacherFirstName;
        private String teacherLastName;
    }
}
