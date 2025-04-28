package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final RosterRepository rosterRepository;
    private final UserRepository userRepository;

    // === Update Grades ===
    // === Update or Create Grade for Student in Roster ===
    public Grade updateGrades(String studentId, Long rosterId, List<Float> performanceScores, List<Float> quizScores, List<Float> quarterlyExamScores) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        // Try to find existing grade
        Grade grade = roster.getGrades().stream()
                .filter(g -> g.getStudent() != null && g.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseGet(() -> {
                    // Create new grade if not found
                    Grade newGrade = Grade.builder()
                            .student(student)
                            .roster(roster)
                            .performanceScores(new ArrayList<>())
                            .quizScores(new ArrayList<>())
                            .quarterlyExamScores(new ArrayList<>())
                            .finalGpa(0f)
                            .build();
                    roster.getGrades().add(newGrade);
                    return newGrade;
                });

        // Update grade scores
        grade.setPerformanceScores(performanceScores);
        grade.setQuizScores(quizScores);
        grade.setQuarterlyExamScores(quarterlyExamScores);

        // Calculate GPA
        float finalGpa = calculateFinalGpa(performanceScores, quizScores, quarterlyExamScores);
        grade.setFinalGpa(finalGpa);

        gradeRepository.save(grade);
        rosterRepository.save(roster); // Save roster to update classGpa + grade list

        updateRosterClassGpa(rosterId);

        return grade;
    }


    // === Calculate GPA ===
    private float calculateFinalGpa(List<Float> performance, List<Float> quizzes, List<Float> exams) {
        float perfAvg = performance.isEmpty() ? 0 : (float) performance.stream().mapToDouble(Float::doubleValue).average().orElse(0);
        float quizAvg = quizzes.isEmpty() ? 0 : (float) quizzes.stream().mapToDouble(Float::doubleValue).average().orElse(0);
        float examAvg = exams.isEmpty() ? 0 : (float) exams.stream().mapToDouble(Float::doubleValue).average().orElse(0);

        return (perfAvg * 0.4f) + (quizAvg * 0.4f) + (examAvg * 0.2f);
    }

    // === Update Roster Class GPA ===
    private void updateRosterClassGpa(Long rosterId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        List<Grade> grades = roster.getGrades();
        if (grades.isEmpty()) {
            roster.setClassGpa(0f);
        } else {
            float avg = (float) grades.stream()
                    .mapToDouble(g -> g.getFinalGpa() == null ? 0 : g.getFinalGpa())
                    .average()
                    .orElse(0);
            roster.setClassGpa(avg);
        }

        rosterRepository.save(roster);
    }

    // === Delete Grade ===
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + gradeId));
        gradeRepository.delete(grade);
    }

    // === Get Grade by ID ===
    public Grade getGradeById(Long id) {
        return gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with ID: " + id));
    }

    // === Get Grades by Student ID ===
    public List<Grade> getGradesByStudentId(String studentId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        return gradeRepository.findByStudentId(student.getId());
    }

    // === Get Grades by Roster ID ===
    public List<Grade> getGradesByRosterId(Long rosterId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        return gradeRepository.findByRosterId(roster.getId());
    }
}
