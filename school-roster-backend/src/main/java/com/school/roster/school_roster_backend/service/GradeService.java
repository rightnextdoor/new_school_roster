package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.enums.CategoryType;
import com.school.roster.school_roster_backend.entity.enums.OperationType;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.HighestPossibleScoreRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    private final RosterService rosterService;
    private final HighestPossibleScoreRepository hpsRepository;

    public Grade createGrade(Roster roster, User student) {
        HighestPossibleScore hps = hpsRepository.findByRosterId(roster.getId())
                .orElseThrow(() -> new RuntimeException("HPS not found for roster " + roster.getId()));

        var hpsDetails = hps.getScoreDetails();

        // Initialize embedded ScoreDetails to match HPS lists (one '0' per slot)
        var gDetails = new com.school.roster.school_roster_backend.entity.embedded.ScoreDetails();
        // For each HPS list, replicate its length with zeros
        hpsDetails.getPerformanceScores().forEach(maxVal -> gDetails.getPerformanceScores().add(0));
        hpsDetails.getQuizScores().forEach(maxVal -> gDetails.getQuizScores().add(0));
        hpsDetails.getQuarterlyExamScores().forEach(maxVal -> gDetails.getQuarterlyExamScores().add(0));

        // Totals, ps, ws remain zero because all scores = 0
        gDetails.recalcTotals();

        Grade grade = Grade.builder()
                .student(student)
                .roster(roster)
                .scoreDetails(gDetails)
                .initialGrade(0.0)
                .finalStatus(calculateGradeStatus(0.0))
                .build();

        Grade saved = gradeRepository.save(grade);

        // 7. Recalculate classGpa for the roster
        rosterService.recalculateClassGpa(roster.getId());

        return saved;
    }

    public Grade updateGrade(Long gradeId, List<Integer> newPerformanceScores,
                             List<Integer> newQuizScores,
                             List<Integer> newQuarterlyExamScores) {
        // 1. Fetch Grade and its Roster/HPS
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found: " + gradeId));
        Long rosterId = grade.getRoster().getId();

        HighestPossibleScore hps = hpsRepository.findByRosterId(rosterId)
                .orElseThrow(() -> new RuntimeException("HPS not found for roster " + rosterId));
        var hpsDetails = hps.getScoreDetails();

        // 2. Overwrite grade’s embedded lists (then clamp)
        var gDetails = grade.getScoreDetails();
        gDetails.setPerformanceScores(newPerformanceScores);
        gDetails.setQuizScores(newQuizScores);
        gDetails.setQuarterlyExamScores(newQuarterlyExamScores);

        // 3. Clamp to [0, hpsMax] and recalc totals → ps → ws → initialGrade → status
        gDetails.clampScoresAgainst(hpsDetails);
        recalcSingleGrade(grade, hpsDetails);

        // 4. Persist updated grade
        Grade updated = gradeRepository.save(grade);

        // 5. Recalculate classGpa
        rosterService.recalculateClassGpa(rosterId);

        return updated;
    }

    private StudentGradeStatus calculateGradeStatus(Double gpa) {
        if (gpa == null) return StudentGradeStatus.FAILED;
        if (gpa >= 98) return StudentGradeStatus.WITH_HIGHEST_HONORS;
        if (gpa >= 95) return StudentGradeStatus.WITH_HIGH_HONORS;
        if (gpa >= 90) return StudentGradeStatus.WITH_HONORS;
        if (gpa >= 75) return StudentGradeStatus.PASSED;
        return StudentGradeStatus.FAILED;
    }

    // === Delete Grade ===
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found: " + gradeId));
        Long rosterId = grade.getRoster().getId();

        gradeRepository.delete(grade);
        // After deletion, recalc classGpa
        rosterService.recalculateClassGpa(rosterId);
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

    public StudentGpaResponse getMyGpa(String studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);

        List<SubjectGrade> subjects = new ArrayList<>();
        double sumInitial = 0.0;
        int count = 0;

        for (Grade grade : grades) {
            // Skip any grade without a roster or without an initialGrade
            if (grade.getRoster() == null || grade.getInitialGrade() == null) {
                continue;
            }

            ScoreDetails details = grade.getScoreDetails();

            // Build a SubjectGrade containing all lists, totals, PS, WS, and initialGrade
            SubjectGrade sg = new SubjectGrade(
                    // 1) Subject name
                    grade.getRoster().getSubjectName(),

                    // 2) initialGrade
                    grade.getInitialGrade(),

                    // ─── PERFORMANCE breakdown ─────────────────────────────────
                    new ArrayList<>(details.getPerformanceScores()),
                    details.getPerformanceTotal(),
                    details.getPerformancePs(),
                    details.getPerformanceWs(),

                    // ─── QUIZ breakdown ────────────────────────────────────────
                    new ArrayList<>(details.getQuizScores()),
                    details.getQuizTotal(),
                    details.getQuizPs(),
                    details.getQuizWs(),

                    // ─── EXAM breakdown ────────────────────────────────────────
                    new ArrayList<>(details.getQuarterlyExamScores()),
                    details.getQuarterlyExamTotal(),
                    details.getQuarterlyExamPs(),
                    details.getQuarterlyExamWs()
            );

            subjects.add(sg);
            sumInitial += grade.getInitialGrade();
            count++;
        }

        // Compute overall GPA as the average of all initialGrade values, rounded to two decimals
        double overallGpa = (count > 0) ? (sumInitial / count) : 0.0;
        double roundedGpa = Math.round(overallGpa * 100.0) / 100.0;

        return new StudentGpaResponse(subjects, roundedGpa);
    }


    // Only showing the new helper methods to add at the bottom of GradeService:

    public boolean canUpdateGrade(Long gradeId, User currentUser) {
        Grade grade = getGradeById(gradeId);
        Roster roster = grade.getRoster();

        return roster != null &&
                (roster.getTeacher() != null && roster.getTeacher().getId().equals(currentUser.getId())
                        || currentUser.getRoles().stream().anyMatch(role ->
                        role.name().equals("ADMIN") ||
                                role.name().equals("ADMINISTRATOR") ||
                                role.name().equals("TEACHER_LEAD")));
    }

    public boolean canDeleteGrade(Long gradeId, User currentUser) {
        Grade grade = getGradeById(gradeId);
        Roster roster = grade.getRoster();

        return roster != null &&
                (roster.getTeacher() != null && roster.getTeacher().getId().equals(currentUser.getId())
                        || currentUser.getRoles().stream().anyMatch(role ->
                        role.name().equals("ADMIN") ||
                                role.name().equals("ADMINISTRATOR")));
    }

    public boolean canViewGrade(Long gradeId, User currentUser) {
        Grade grade = getGradeById(gradeId);

        return (grade.getStudent() != null && grade.getStudent().getId().equals(currentUser.getId())) // owner student
                || (grade.getRoster() != null && rosterService.canViewRoster(grade.getRoster().getId(), currentUser)); // teacher/admin
    }

    public void propagateSlotChange(Long rosterId, CategoryType category, OperationType operation, Integer index) {
        // 1. Fetch HPS to get current max‐values and totals
        HighestPossibleScore hps = hpsRepository.findByRosterId(rosterId)
                .orElseThrow(() -> new RuntimeException("HPS not found for roster " + rosterId));
        var hpsDetails = hps.getScoreDetails();

        // 2. Fetch all grades in that roster
        List<Grade> grades = gradeRepository.findByRosterId(rosterId);

        for (Grade grade : grades) {
            var gDetails = grade.getScoreDetails();

            // 3. Add or remove the corresponding slot in Grade’s embedded lists
            switch (category) {
                case PERFORMANCE:
                    if (operation == OperationType.ADD) {
                        gDetails.getPerformanceScores().add(0);
                    } else if(operation == OperationType.REMOVE){
                        gDetails.getPerformanceScores().remove((int) index);
                    } else {
                        break;
                    }
                    break;
                case QUIZ:
                    if (operation == OperationType.ADD) {
                        gDetails.getQuizScores().add(0);
                    } else if(operation == OperationType.REMOVE){
                        gDetails.getQuizScores().remove((int) index);
                    } else {
                        break;
                    }
                    break;
                case EXAM:
                    if (operation == OperationType.ADD) {
                        gDetails.getQuarterlyExamScores().add(0);
                    } else if(operation == OperationType.REMOVE){
                        gDetails.getQuarterlyExamScores().remove((int) index);
                    } else {
                        break;
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unknown category: " + category);
            }

            // 4. Clamp grade’s scores against new HPS maxima
            gDetails.clampScoresAgainst(hpsDetails);

            // 5. Recalculate totals → ps → ws → initialGrade → finalStatus
            recalcSingleGrade(grade, hpsDetails);

            // 6. Persist updated grade
            gradeRepository.save(grade);

            // 7. Recalculate class GPA for this roster after each grade update
            rosterService.recalculateClassGpa(rosterId);
        }
    }

    public void updateAllGradesPs(Long rosterId, HighestPossibleScore hps) {
        var hpsDetails = hps.getScoreDetails();

        // Fetch all grades
        List<Grade> grades = gradeRepository.findByRosterId(rosterId);

        for (Grade grade : grades) {
            var gDetails = grade.getScoreDetails();

            // Recalculate PS (grade.ps = (gradeTotal / hpsTotal) * hpsPs)
            gDetails.recalcPs(hpsDetails);

            // Recalculate WS (grade.ws = grade.ps * hps.ws) and initialGrade
            gDetails.recalcWs(hpsDetails);

            // Compute and set initialGrade = sum of the three ws fields
            double initialSum = gDetails.getPerformanceWs()
                    + gDetails.getQuizWs()
                    + gDetails.getQuarterlyExamWs();
            grade.setInitialGrade(roundToTwoDecimals(initialSum));

            // Assign finalStatus based on initialGrade
            grade.setFinalStatus(calculateGradeStatus(grade.getInitialGrade()));

            // Save grade
            gradeRepository.save(grade);

            // Update classGpa after each grade change
            rosterService.recalculateClassGpa(rosterId);
        }
    }

    public void updateAllGradesWs(Long rosterId, HighestPossibleScore hps) {
        var hpsDetails = hps.getScoreDetails();

        // Fetch all grades
        List<Grade> grades = gradeRepository.findByRosterId(rosterId);

        for (Grade grade : grades) {
            var gDetails = grade.getScoreDetails();

            // Recalculate WS (grade.ws = grade.ps * hps.ws)
            gDetails.recalcWs(hpsDetails);

            // Compute and set new initialGrade
            double initialSum = gDetails.getPerformanceWs()
                    + gDetails.getQuizWs()
                    + gDetails.getQuarterlyExamWs();
            grade.setInitialGrade(roundToTwoDecimals(initialSum));

            // Assign finalStatus
            grade.setFinalStatus(calculateGradeStatus(grade.getInitialGrade()));

            // Save grade
            gradeRepository.save(grade);

            // Update classGpa
            rosterService.recalculateClassGpa(rosterId);
        }
    }

    private void recalcSingleGrade(Grade grade, com.school.roster.school_roster_backend.entity.embedded.ScoreDetails hpsDetails) {
        var gDetails = grade.getScoreDetails();

        // (1) Recalculate totals for this grade
        gDetails.recalcTotals();

        // (2) Recalculate PS: (gradeTotal / hpsTotal) * hpsPs
        gDetails.recalcPs(hpsDetails);

        // (3) Recalculate WS: gradePs * hpsWs
        gDetails.recalcWs(hpsDetails);

        // (4) Compute initialGrade = sum of the three ws values
        double initialSum = gDetails.getPerformanceWs()
                + gDetails.getQuizWs()
                + gDetails.getQuarterlyExamWs();
        grade.setInitialGrade(roundToTwoDecimals(initialSum));

        // (5) Determine finalStatus based on initialGrade
        grade.setFinalStatus(calculateGradeStatus(grade.getInitialGrade()));
    }

    private Double roundToTwoDecimals(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }


    // === DTOs ===
    @Data
    @AllArgsConstructor
    public static class StudentGpaResponse {
        private List<SubjectGrade> subjects;
        private Double studentGpa;         // previously was Float or double; now Double
    }

    @Data
    @AllArgsConstructor
    public static class SubjectGrade {
        private String subjectName;
        private Double initialGrade;

        // ─── PERFORMANCE breakdown ───────────────────────────────────
        private List<Integer> performanceScores;
        private Integer performanceTotal;
        private Double performancePs;
        private Double performanceWs;

        // ─── QUIZ breakdown ─────────────────────────────────────────
        private List<Integer> quizScores;
        private Integer quizTotal;
        private Double quizPs;
        private Double quizWs;

        // ─── EXAM breakdown ─────────────────────────────────────────
        private List<Integer> quarterlyExamScores;
        private Integer quarterlyExamTotal;
        private Double quarterlyExamPs;
        private Double quarterlyExamWs;
    }
}
