package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.repository.GradeRepository;
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

    @InjectMocks
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private StudentGradeStatus invokeCalculateGradeStatus(Float gpa) {
        try {
            var method = GradeService.class.getDeclaredMethod("calculateGradeStatus", Float.class);
            method.setAccessible(true);
            return (StudentGradeStatus) method.invoke(gradeService, gpa);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // === updateGrades ===
    @Test
    void updateGrades_existingGrade_shouldUpdateAndReturn() {
        String studentId = "student1";
        Long rosterId = 1L;

        User student = new User();
        student.setId(studentId);

        Roster roster = new Roster();
        roster.setId(rosterId);

        Grade grade = Grade.builder()
                .student(student)
                .roster(roster)
                .performanceScores(new ArrayList<>())
                .quizScores(new ArrayList<>())
                .quarterlyExamScores(new ArrayList<>())
                .build();
        roster.setGrades(List.of(grade));

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(rosterRepository.findById(rosterId)).thenReturn(Optional.of(roster));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Float> performance = List.of(90f, 95f);
        List<Float> quizzes = List.of(88f, 92f);
        List<Float> exams = List.of(85f, 87f);

        Grade result = gradeService.updateGrades(studentId, rosterId, performance, quizzes, exams);

        assertNotNull(result);
        assertEquals(grade, result);
        assertNotNull(result.getFinalGpa());
    }

    @Test
    void updateGrades_newGrade_shouldCreateAndReturn() {
        String studentId = "student2";
        Long rosterId = 2L;

        User student = new User();
        student.setId(studentId);

        Roster roster = new Roster();
        roster.setId(rosterId);
        roster.setGrades(new ArrayList<>());

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(rosterRepository.findById(rosterId)).thenReturn(Optional.of(roster));
        when(gradeRepository.save(any(Grade.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Grade result = gradeService.updateGrades(studentId, rosterId, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertNotNull(result);
        assertEquals(student, result.getStudent());
    }

    // === calculateGradeStatus ===
    @Test
    void calculateGradeStatus_shouldReturnCorrectStatus() {
        assertEquals(StudentGradeStatus.WITH_HIGHEST_HONORS, invokeCalculateGradeStatus(99f));
        assertEquals(StudentGradeStatus.WITH_HIGH_HONORS, invokeCalculateGradeStatus(96f));
        assertEquals(StudentGradeStatus.WITH_HONORS, invokeCalculateGradeStatus(92f));
        assertEquals(StudentGradeStatus.PASSED, invokeCalculateGradeStatus(80f));
        assertEquals(StudentGradeStatus.FAILED, invokeCalculateGradeStatus(50f));
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
        when(rosterRepository.findById(roster.getId())).thenReturn(Optional.of(roster));

        gradeService.deleteGrade(gradeId);

        verify(gradeRepository).delete(grade);
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

    // === getGradesByRosterId ===
    @Test
    void getGradesByRosterId_shouldReturnGrades() {
        Long rosterId = 6L;
        Roster roster = new Roster();
        roster.setId(rosterId);

        when(rosterRepository.findById(rosterId)).thenReturn(Optional.of(roster));
        when(gradeRepository.findByRosterId(rosterId)).thenReturn(List.of(new Grade(), new Grade()));

        List<Grade> result = gradeService.getGradesByRosterId(rosterId);

        assertEquals(2, result.size());
    }

    // === getMyGpa ===
    @Test
    void getMyGpa_shouldReturnCorrectSummary() {
        String studentId = "student4";

        Grade grade1 = Grade.builder().finalGpa(90f).roster(Roster.builder().subjectName("Math").build()).build();
        Grade grade2 = Grade.builder().finalGpa(80f).roster(Roster.builder().subjectName("Science").build()).build();

        when(gradeRepository.findByStudentId(studentId)).thenReturn(List.of(grade1, grade2));

        GradeService.StudentGpaResponse response = gradeService.getMyGpa(studentId);

        assertEquals(2, response.getSubjects().size());
        assertEquals(85.0f, response.getStudentGpa(), 0.01f);
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

        assertTrue(gradeService.canUpdateGrade(gradeId, teacher));
    }

    // === canDeleteGrade ===
    @Test
    void canDeleteGrade_shouldReturnTrueForAdmin() {
        Long gradeId = 8L;
        User admin = new User();
        admin.setId("admin1");
        admin.setRoles(Set.of(Role.ADMIN));

        Roster roster = new Roster();
        roster.setTeacher(null);

        Grade grade = Grade.builder().roster(roster).build();
        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        assertTrue(gradeService.canDeleteGrade(gradeId, admin));
    }

    // === canViewGrade ===
    @Test
    void canViewGrade_shouldAllowStudent() {
        Long gradeId = 9L;
        User student = new User();
        student.setId("student5");

        Grade grade = Grade.builder()
                .student(student)
                .build();

        when(gradeRepository.findById(gradeId)).thenReturn(Optional.of(grade));

        assertTrue(gradeService.canViewGrade(gradeId, student));
    }

    @Test
    void test_continue_logic_skips_invalid_grades() {
        Grade invalidGrade1 = new Grade(); // No roster, no GPA
        Grade invalidGrade2 = new Grade();
        invalidGrade2.setRoster(new Roster()); // Roster set but no GPA
        Grade invalidGrade3 = new Grade();
        invalidGrade3.setFinalGpa(95f); // GPA set but no roster

        List<Grade> grades = List.of(invalidGrade1, invalidGrade2, invalidGrade3);

        float totalGpa = grades.stream()
                .filter(g -> g.getRoster() != null && g.getFinalGpa() != null)
                .map(Grade::getFinalGpa)
                .reduce(0f, Float::sum);

        assertEquals(0f, totalGpa); // All should be skipped, totalGpa remains 0
    }

    @Test
    void canUpdateGrade_shouldAllowWhenTeacherOrRoleMatches() {
        User teacher = new User();
        teacher.setId("teacher123");

        Roster roster = new Roster();
        roster.setTeacher(teacher);

        Grade grade = new Grade();
        grade.setId(1L);
        grade.setRoster(roster);

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        // Teacher is the roster's teacher
        teacher.setRoles(Set.of(Role.TEACHER));
        assertTrue(gradeService.canUpdateGrade(1L, teacher));

        // Teacher has ADMIN role
        teacher.setRoles(Set.of(Role.ADMIN));
        assertTrue(gradeService.canUpdateGrade(1L, teacher));

        // Teacher has ADMINISTRATOR role
        teacher.setRoles(Set.of(Role.ADMINISTRATOR));
        assertTrue(gradeService.canUpdateGrade(1L, teacher));

        // Teacher has TEACHER_LEAD role
        teacher.setRoles(Set.of(Role.TEACHER_LEAD));
        assertTrue(gradeService.canUpdateGrade(1L, teacher));
    }


    @Test
    void canDeleteGrade_shouldAllowOnlyForAdministrator() {
        // Create a roster
        Roster roster = new Roster();

        // Create a grade with that roster
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setRoster(roster);

        // Mock grade repository
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        // Create an administrator user
        User admin = new User();
        admin.setId("adminId");
        admin.setRoles(Set.of(Role.ADMINISTRATOR));

        // Call the method
        boolean result = gradeService.canDeleteGrade(1L, admin);

        // Verify
        assertTrue(result, "Expected user with ADMINISTRATOR role to be allowed to delete grade.");
    }


    @Test
    void canViewGrade_shouldAllowIfStudentOrCanViewRoster() {
        // Case 1: Student is the owner of the grade
        User student = new User();
        student.setId("student123");

        Grade grade = new Grade();
        grade.setId(1L);
        grade.setStudent(student);

        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        assertTrue(gradeService.canViewGrade(1L, student));

        // Case 2: Not the student, but can view roster
        Roster roster = new Roster();
        roster.setId(999L);
        Grade grade2 = new Grade();
        grade2.setId(2L);
        grade2.setRoster(roster);

        User viewer = new User();
        viewer.setId("viewer1");

        when(gradeRepository.findById(2L)).thenReturn(Optional.of(grade2));
        when(rosterService.canViewRoster(999L, viewer)).thenReturn(true);

        assertTrue(gradeService.canViewGrade(2L, viewer));
    }


    @Test
    void test_subject_grade_constructor() {
        GradeService.SubjectGrade sg = new GradeService.SubjectGrade("Math", 92.5f);
        assertEquals("Math", sg.getSubjectName());
        assertEquals(92.5f, sg.getGpa());
    }

    @Test
    void getMyGpa_shouldSkipGradesWithNullRosterOrGpa() {
        // Setup
        Grade grade1 = new Grade(); // Should be skipped (null roster)
        grade1.setFinalGpa(90f);

        Grade grade2 = new Grade(); // Should be skipped (null GPA)
        grade2.setRoster(new Roster());
        grade2.setFinalGpa(null);

        Grade validGrade = new Grade(); // Should be counted
        Roster roster = new Roster();
        roster.setSubjectName("Math");
        validGrade.setRoster(roster);
        validGrade.setFinalGpa(85f);

        when(gradeRepository.findByStudentId("student123"))
                .thenReturn(List.of(grade1, grade2, validGrade));

        // Act
        GradeService.StudentGpaResponse response = gradeService.getMyGpa("student123");

        // Assert
        assertEquals(85f, response.getStudentGpa());
        assertEquals(1, response.getSubjects().size());
        assertEquals("Math", response.getSubjects().get(0).getSubjectName());
    }


    @Test
    void canUpdateGrade_shouldAllowForAllAuthorizedRoles() {
        Grade grade = new Grade();
        grade.setId(42L);
        Roster roster = new Roster();
        roster.setTeacher(null); // Skip teacher match
        grade.setRoster(roster);

        when(gradeRepository.findById(42L)).thenReturn(Optional.of(grade));

        for (Role role : List.of(Role.ADMIN, Role.ADMINISTRATOR, Role.TEACHER_LEAD)) {
            User user = new User();
            user.setId("anyId");
            user.setRoles(Set.of(role));

            boolean result = gradeService.canUpdateGrade(42L, user);
            assertTrue(result, "Expected " + role.name() + " to be allowed to update grade.");
        }
    }


}
