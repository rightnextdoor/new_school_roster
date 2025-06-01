package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.HighestPossibleScoreRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private RosterRepository rosterRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RosterService rosterService;
    @Mock
    private HighestPossibleScoreRepository hpsRepository;

    @InjectMocks
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Reflectively invoke the private calculateGradeStatus(Double) method in GradeService.
     * (Changed from Float to Double to match the current signature:
     *   private StudentGradeStatus calculateGradeStatus(Double gpa) {...}
     * :contentReference[oaicite:2]{index=2}, :contentReference[oaicite:3]{index=3})
     */
    private StudentGradeStatus invokeCalculateGradeStatus(Double gpa) {
        try {
            var method = GradeService.class.getDeclaredMethod("calculateGradeStatus", Double.class);
            method.setAccessible(true);
            return (StudentGradeStatus) method.invoke(gradeService, gpa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // === updateGrade ===
    @Test
    void updateGrade_existingGrade_shouldUpdateAndReturn() {
        String studentId = "student1";
        Long rosterId = 1L;
        Long gradeId   = 100L;

        // Setup a Grade with an existing roster
        User student = new User();
        student.setId(studentId);

        Roster roster = new Roster();
        roster.setId(rosterId);
        roster.setGrades(new ArrayList<>());

        Grade existing = Grade.builder()
                .id(gradeId)
                .student(student)
                .roster(roster)
                // embed an empty ScoreDetails so that recalcSingleGrade(...) can run later
                .scoreDetails(new com.school.roster.school_roster_backend.entity.embedded.ScoreDetails())
                .initialGrade(0.0)
                .finalStatus(StudentGradeStatus.FAILED)
                .build();
        roster.getGrades().add(existing);

        // Stub repositories/service
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(existing));
        // HPS lookup: return a HighestPossibleScore with ScoreDetails (stubbed to match lengths)
        HighestPossibleScore stubHps = new HighestPossibleScore();
        stubHps.setRoster(roster);
        stubHps.setScoreDetails(new com.school.roster.school_roster_backend.entity.embedded.ScoreDetails());
        // e.g. two performance slots, two quiz slots, one exam slot
        stubHps.getScoreDetails().getPerformanceScores().addAll(List.of(10, 10));
        stubHps.getScoreDetails().getQuizScores().addAll(List.of(20, 20));
        stubHps.getScoreDetails().getQuarterlyExamScores().addAll(List.of(30));
        when(hpsRepository.findByRosterId(rosterId)).thenReturn(Optional.of(stubHps));

        // Stub save to return the same instance
        when(gradeRepository.save(any(Grade.class))).thenAnswer(inv -> inv.getArgument(0));

        // Provide new raw lists (clamped/clamping happens inside recalc)
        List<Integer> performance = List.of(8, 9);
        List<Integer> quizzes     = List.of(15, 18);
        List<Integer> exams       = List.of(25);

        Grade result = gradeService.updateGrade(
                gradeId,
                performance,
                quizzes,
                exams
        );

        assertNotNull(result);
        assertEquals(gradeId, result.getId());
        // finalGpa (initialGrade) should have been recalculated internally, so it must be non-null
        assertNotNull(result.getInitialGrade());
        // finalStatus must come from calculateGradeStatus(Double)
        assertNotNull(result.getFinalStatus());
    }

    @Test
    void updateGrade_newGradeNotFound_shouldThrow() {
        Long nonexistent = 999L;
        when(gradeRepository.findById(nonexistent)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                gradeService.updateGrade(nonexistent, List.of(), List.of(), List.of())
        );
        assertTrue(ex.getMessage().contains("Grade not found"));
    }

    // === calculateGradeStatus ===
    @Test
    void calculateGradeStatus_shouldReturnCorrectStatus() {
        assertEquals(StudentGradeStatus.WITH_HIGHEST_HONORS, invokeCalculateGradeStatus(99.0));
        assertEquals(StudentGradeStatus.WITH_HIGH_HONORS, invokeCalculateGradeStatus(96.0));
        assertEquals(StudentGradeStatus.WITH_HONORS, invokeCalculateGradeStatus(92.0));
        assertEquals(StudentGradeStatus.PASSED, invokeCalculateGradeStatus(80.0));
        assertEquals(StudentGradeStatus.FAILED, invokeCalculateGradeStatus(50.0));
        assertEquals(StudentGradeStatus.FAILED, invokeCalculateGradeStatus(null));
    }

    // === deleteGrade ===
    @Test
    void deleteGrade_existingGrade_shouldDelete() {
        Long gradeId = 3L;
        Roster roster = new Roster();
        roster.setId(10L);

        Grade grade = Grade.builder()
                .id(gradeId)
                .roster(roster)
                .build();
        roster.setGrades(new ArrayList<>(List.of(grade)));

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));
        // Delete does not return anything, so just verify it's called
        doNothing().when(gradeRepository).delete(grade);

        gradeService.deleteGrade(gradeId);
        verify(gradeRepository).delete(grade);
        verify(rosterService).recalculateClassGpa(roster.getId());
    }

    @Test
    void deleteGrade_nonexistent_shouldThrow() {
        Long missing = 5L;
        when(gradeRepository.findById(missing)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> gradeService.deleteGrade(missing));
        assertTrue(ex.getMessage().contains("Grade not found"));
    }

    // === getGradeById ===
    @Test
    void getGradeById_existingGrade_shouldReturn() {
        Long gradeId = 5L;
        Grade grade = new Grade();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        Grade found = gradeService.getGradeById(gradeId);
        assertEquals(grade, found);
    }

    @Test
    void getGradeById_notFound_shouldThrow() {
        Long missing = 7L;
        when(gradeRepository.findById(missing)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> gradeService.getGradeById(missing));
        assertTrue(ex.getMessage().contains("Grade not found with ID"));
    }

    // === getGradesByStudentId ===
    @Test
    void getGradesByStudentId_shouldReturnGrades() {
        String studentId = "student3";
        User student = new User();
        student.setId(studentId);

        List<Grade> grades = List.of(new Grade(), new Grade());

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(gradeRepository.findByStudentId(studentId)).thenReturn(grades);

        List<Grade> result = gradeService.getGradesByStudentId(studentId);

        assertEquals(2, result.size());
    }

    @Test
    void getGradesByStudentId_studentNotFound_shouldThrow() {
        String badId = "missing";
        when(userRepository.findById(badId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gradeService.getGradesByStudentId(badId));
        assertTrue(ex.getMessage().contains("Student not found"));
    }

    // === getGradesByRosterId ===
    @Test
    void getGradesByRosterId_shouldReturnGrades() {
        Long rosterId = 6L;
        Roster roster = new Roster();
        roster.setId(rosterId);

        List<Grade> grades = List.of(new Grade(), new Grade());
        when(rosterRepository.findById(rosterId)).thenReturn(Optional.of(roster));
        when(gradeRepository.findByRosterId(rosterId)).thenReturn(grades);

        List<Grade> result = gradeService.getGradesByRosterId(rosterId);
        assertEquals(2, result.size());
    }

    @Test
    void getGradesByRosterId_notFound_shouldThrow() {
        Long badRoster = 11L;
        when(rosterRepository.findById(badRoster)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> gradeService.getGradesByRosterId(badRoster));
        assertTrue(ex.getMessage().contains("Roster not found"));
    }

    // === getMyGpa ===
    @Test
    void getMyGpa_shouldReturnCorrectSummary() {
        String studentId = "student4";

        // Create two Grades, each with a non‐null ScoreDetails()
        Grade grade1 = Grade.builder()
                .initialGrade(90.0)
                .roster(Roster.builder().subjectName("Math").build())
                .scoreDetails(new ScoreDetails())  // ← ensure it’s not null
                .build();

        Grade grade2 = Grade.builder()
                .initialGrade(80.0)
                .roster(Roster.builder().subjectName("Science").build())
                .scoreDetails(new ScoreDetails())  // ← ensure it’s not null
                .build();

        when(gradeRepository.findByStudentId(studentId))
                .thenReturn(List.of(grade1, grade2));

        GradeService.StudentGpaResponse response = gradeService.getMyGpa(studentId);

        assertEquals(2, response.getSubjects().size());
        assertEquals(85.0f, response.getStudentGpa(), 0.001f);
    }

    @Test
    void getMyGpa_shouldSkipGradesWithNullRosterOrNullInitialGrade() {
        Grade invalid1 = new Grade();
        invalid1.setInitialGrade(null);
        invalid1.setRoster(new Roster());

        Grade invalid2 = new Grade();
        invalid2.setInitialGrade(75.0);
        invalid2.setRoster(null);

        Grade valid = new Grade();
        valid.setInitialGrade(85.0);
        Roster r = new Roster();
        r.setSubjectName("Math");
        valid.setRoster(r);

        when(gradeRepository.findByStudentId("student123"))
                .thenReturn(List.of(invalid1, invalid2, valid));

        GradeService.StudentGpaResponse response = gradeService.getMyGpa("student123");
        assertEquals(1, response.getSubjects().size());
        assertEquals("Math", response.getSubjects().get(0).getSubjectName());
        assertEquals(85.0, response.getStudentGpa(), 0.001);
    }

    // === canUpdateGrade ===
    @Test
    void canUpdateGrade_shouldReturnTrueForOwnerTeacher() {
        Long gradeId = 7L;
        User teacher = new User();
        teacher.setId("teacher1");

        Roster roster = new Roster();
        roster.setTeacher(teacher);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        teacher.setRoles(Set.of(Role.TEACHER));
        assertTrue(gradeService.canUpdateGrade(gradeId, teacher));
    }

    @Test
    void canUpdateGrade_shouldAllowForAdminOrAdministorOrTeacherLead() {
        Long gradeId = 8L;
        Roster roster = new Roster();
        roster.setTeacher(null);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        for (Role r : List.of(Role.ADMIN, Role.ADMINISTRATOR, Role.TEACHER_LEAD)) {
            User user = new User();
            user.setId("any");
            user.setRoles(Set.of(r));
            assertTrue(gradeService.canUpdateGrade(gradeId, user), "Expected " + r + " to be allowed");
        }
    }

    @Test
    void canUpdateGrade_shouldReturnFalseOtherwise() {
        Long gradeId = 9L;
        Roster roster = new Roster();
        roster.setTeacher(null);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        User user = new User();
        user.setId("other");
        user.setRoles(Set.of());  // no roles
        assertFalse(gradeService.canUpdateGrade(gradeId, user));
    }

    // === canDeleteGrade ===
    @Test
    void canDeleteGrade_shouldAllowForAdminOrAdministrator() {
        Long gradeId = 10L;
        Roster roster = new Roster();
        roster.setTeacher(null);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        for (Role r : List.of(Role.ADMIN, Role.ADMINISTRATOR)) {
            User user = new User();
            user.setRoles(Set.of(r));
            assertTrue(gradeService.canDeleteGrade(gradeId, user), "Expected " + r + " to be allowed");
        }
    }

    @Test
    void canDeleteGrade_shouldReturnFalseOtherwise() {
        Long gradeId = 11L;
        Roster roster = new Roster();
        roster.setTeacher(null);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        User user = new User();
        user.setRoles(Set.of(Role.TEACHER)); // teacher is not allowed to delete, only Admin/Administor
        assertFalse(gradeService.canDeleteGrade(gradeId, user));
    }

    // === canViewGrade ===
    @Test
    void canViewGrade_shouldAllowStudentOwner() {
        Long gradeId = 12L;
        User student = new User();
        student.setId("student5");

        Grade grade = Grade.builder().student(student).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        assertTrue(gradeService.canViewGrade(gradeId, student));
    }

    @Test
    void canViewGrade_shouldAllowWhenRosterViewableByTeacherOrAdmin() {
        Long gradeId = 13L;
        Roster roster = new Roster();
        roster.setId(999L);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        User viewer = new User();
        viewer.setRoles(Set.of(Role.TEACHER)); // assume rosterService.canViewRoster returns true
        when(rosterService.canViewRoster(999L, viewer)).thenReturn(true);

        assertTrue(gradeService.canViewGrade(gradeId, viewer));
    }

    @Test
    void canViewGrade_shouldReturnFalseOtherwise() {
        Long gradeId = 14L;
        Roster roster = new Roster();
        roster.setId(1000L);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        User viewer = new User();
        viewer.setRoles(Set.of()); // no roles, not the student, not roster-viewable
        when(rosterService.canViewRoster(1000L, viewer)).thenReturn(false);
        assertFalse(gradeService.canViewGrade(gradeId, viewer));
    }

    // === SubjectGrade constructor ===
    @Test
    void test_subject_grade_constructor() {
        GradeService.SubjectGrade sg = new GradeService.SubjectGrade(
                "Math",
                92.5,
                List.of(85, 90),
                175,
                80.0,
                32.0,
                List.of(88, 92),
                180,
                72.0,
                28.8,
                List.of(87),
                87,
                29.0,
                11.6
        );
        assertEquals("Math", sg.getSubjectName());
        assertEquals(92.5, sg.getInitialGrade());
        assertEquals(32.0, sg.getPerformanceWs());
        assertEquals(List.of(88, 92), sg.getQuizScores());
    }
}
