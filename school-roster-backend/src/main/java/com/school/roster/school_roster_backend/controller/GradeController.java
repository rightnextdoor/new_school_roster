package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.service.GradeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    // === Update Grade ===
    @PutMapping("/update")
    public ResponseEntity<Grade> updateGrade(@RequestBody UpdateGradeRequest request) {
        Grade updatedGrade = gradeService.updateGrades(
                request.getGradeId(),
                request.getPerformanceScores(),
                request.getQuizScores(),
                request.getQuarterlyExamScores()
        );
        return ResponseEntity.ok(updatedGrade);
    }

    // === Delete Grade ===
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteGrade(@RequestBody GradeIdRequest request) {
        gradeService.deleteGrade(request.getGradeId());
        return ResponseEntity.ok("Grade deleted successfully.");
    }

    // === Get Grade by ID ===
    @PostMapping("/getById")
    public ResponseEntity<Grade> getGradeById(@RequestBody GradeIdRequest request) {
        Grade grade = gradeService.getGradeById(request.getGradeId());
        return ResponseEntity.ok(grade);
    }

    // === Get Grades by Roster ===
    @PostMapping("/getByRoster")
    public ResponseEntity<List<Grade>> getGradesByRoster(@RequestBody RosterIdRequest request) {
        List<Grade> grades = gradeService.getGradesByRosterId(request.getRosterId());
        return ResponseEntity.ok(grades);
    }

    // === Get Grades by Student ===
    @PostMapping("/getByStudent")
    public ResponseEntity<List<Grade>> getGradesByStudent(@RequestBody StudentIdRequest request) {
        List<Grade> grades = gradeService.getGradesByStudentId(request.getStudentId());
        return ResponseEntity.ok(grades);
    }

    // === Request DTOs ===
    @Data
    @AllArgsConstructor
    private static class UpdateGradeRequest {
        private Long gradeId;
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
}
