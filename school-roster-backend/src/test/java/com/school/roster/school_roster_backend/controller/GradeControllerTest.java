package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.service.GradeService;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GradeControllerTest {

    private GradeService gradeService;
    private RosterService rosterService;
    private UserService userService;
    private GradeController controller;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        gradeService = mock(GradeService.class);
        rosterService = mock(RosterService.class);
        userService = mock(UserService.class);
        authentication = mock(Authentication.class);
        controller = new GradeController(gradeService, rosterService, userService);

        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void updateGrade_shouldUpdateAndReturnGradeResponse() {
        // Prepare user and authorization
        User user = new User();
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(rosterService.canEditRoster(1L, user)).thenReturn(true);

        // Prepare a Grade with minimal ScoreDetails so that GradeResponse can build
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setInitialGrade(88.55);
        grade.setFinalStatus(StudentGradeStatus.PASSED);

        // Set up Roster inside Grade
        Roster roster = new Roster();
        roster.setId(1L);
        roster.setSubjectName("Math");
        roster.setPeriod("First");
        roster.setNickname("Algebra");
        User teacher = new User();
        var teacherProfile = new com.school.roster.school_roster_backend.entity.NonStudentProfile();
        teacherProfile.setFirstName("John");
        teacherProfile.setLastName("Doe");
        teacher.setNonStudentProfile(teacherProfile);
        roster.setTeacher(teacher);
        grade.setRoster(roster);

        // Set up Student inside Grade
        User student = new User();
        student.setId("student123");
        var studentProfile = new com.school.roster.school_roster_backend.entity.StudentProfile();
        studentProfile.setFirstName("Alice");
        studentProfile.setLastName("Smith");
        student.setStudentProfile(studentProfile);
        grade.setStudent(student);

        // Put some dummy ScoreDetails inside the Grade (so response has non‐empty lists)
        ScoreDetails details = new ScoreDetails();
        details.getPerformanceScores().add(9);
        details.setPerformanceTotal(9);
        details.setPerformancePs(90.0);
        details.setPerformanceWs(36.0);

        details.getQuizScores().add(8);
        details.getQuizScores().add(7);
        details.setQuizTotal(15);
        details.setQuizPs(24.19);
        details.setQuizWs(9.68);

        details.getQuarterlyExamScores().add(18);
        details.setQuarterlyExamTotal(18);
        details.setQuarterlyExamPs(90.0);
        details.setQuarterlyExamWs(18.0);

        grade.setScoreDetails(details);

        // Mock gradeService.updateGrade(...)
        when(gradeService.updateGrade(
                eq(1L),
                anyList(),
                anyList(),
                anyList()
        )).thenReturn(grade);

        // Build request: (rosterId=1, gradeId=1, perf=[9], quiz=[8,7], exam=[18])
        var request = new GradeController.UpdateGradeRequest(
                1L,
                1L,
                List.of(9),
                List.of(8, 7),
                List.of(18)
        );

        ResponseEntity<GradeController.GradeResponse> response =
                controller.updateGrade(request, authentication);

        assertNotNull(response.getBody());
        GradeController.GradeResponse body = response.getBody();

        // Check basic fields
        assertEquals(1L, body.getGradeId());
        assertEquals(88.55, body.getInitialGrade());
        assertEquals("PASSED", body.getFinalStatus());

        // Student fields
        assertEquals("student123", body.getStudentId());
        assertEquals("Alice", body.getStudentFirstName());
        assertEquals("Smith", body.getStudentLastName());

        // Roster fields
        assertEquals(1L, body.getRosterId());
        assertEquals("Math", body.getSubjectName());
        assertEquals("First", body.getPeriod());
        assertEquals("Algebra", body.getNickname());
        assertEquals("John", body.getTeacherFirstName());
        assertEquals("Doe", body.getTeacherLastName());

        // ScoreDetails fields (ensure lists and values carried over)
        assertThat(body.getPerformanceScores()).containsExactly(9);
        assertEquals(9, body.getPerformanceTotal());
        assertEquals(90.0, body.getPerformancePs());
        assertEquals(36.0, body.getPerformanceWs());

        assertThat(body.getQuizScores()).containsExactly(8, 7);
        assertEquals(15, body.getQuizTotal());
        assertEquals(24.19, body.getQuizPs());
        assertEquals(9.68, body.getQuizWs());

        assertThat(body.getQuarterlyExamScores()).containsExactly(18);
        assertEquals(18, body.getQuarterlyExamTotal());
        assertEquals(90.0, body.getQuarterlyExamPs());
        assertEquals(18.0, body.getQuarterlyExamWs());
    }

    @Test
    void deleteGrade_shouldDeleteGradeSuccessfully() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(gradeService.canDeleteGrade(1L, user)).thenReturn(true);

        ResponseEntity<String> response = controller.deleteGrade(
                new GradeController.GradeIdRequest(1L),
                authentication
        );

        verify(gradeService).deleteGrade(1L);
        assertEquals("Grade deleted successfully.", response.getBody());
    }

    @Test
    void getGradeById_shouldReturnGradeResponse() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(gradeService.canViewGrade(1L, user)).thenReturn(true);

        Grade grade = createMockGrade();
        when(gradeService.getGradeById(1L)).thenReturn(grade);

        ResponseEntity<GradeController.GradeResponse> response = controller.getGradeById(
                new GradeController.GradeIdRequest(1L),
                authentication
        );

        GradeController.GradeResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(1L, body.getGradeId());
        assertEquals("Math", body.getSubjectName());
        assertEquals("John", body.getTeacherFirstName());
    }

    @Test
    void getGradesByRoster_shouldReturnListOfGradeResponses() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(rosterService.canViewRoster(1L, user)).thenReturn(true);

        Grade grade = createMockGrade();
        when(gradeService.getGradesByRosterId(1L)).thenReturn(List.of(grade));

        ResponseEntity<List<GradeController.GradeResponse>> response = controller.getGradesByRoster(
                new GradeController.RosterIdRequest(1L),
                authentication
        );

        List<GradeController.GradeResponse> list = response.getBody();
        assertThat(list).hasSize(1);
        assertEquals("Math", list.get(0).getSubjectName());
    }

    @Test
    void getGradesByStudent_shouldReturnListOfGradeResponses() {
        User user = new User();
        user.setId("studentId");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        Grade grade = createMockGrade();
        when(gradeService.getGradesByStudentId("studentId"))
                .thenReturn(List.of(grade));

        ResponseEntity<List<GradeController.GradeResponse>> response = controller.getGradesByStudent(
                new GradeController.StudentIdRequest("studentId"),
                authentication
        );

        List<GradeController.GradeResponse> list = response.getBody();
        assertThat(list).hasSize(1);
        // initialGrade is 90.0 in createMockGrade()
        assertEquals(90.0, list.get(0).getInitialGrade());
    }

    @Test
    void getMyGpa_shouldReturnStudentGpa() {
        User user = new User();
        user.setId("studentId");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        // Mock SubjectGrade list
        GradeService.SubjectGrade sg = new GradeService.SubjectGrade(
                "Math",           // subjectName
                80.0,             // initialGrade
                List.of(8),       // perf scores
                8,                // perf total
                80.0,             // perf Ps
                32.0,             // perf Ws
                List.of(5, 7),    // quiz scores
                12,               // quiz total
                19.35,            // quiz Ps
                7.74,             // quiz Ws
                List.of(18),      // exam scores
                18,               // exam total
                90.0,             // exam Ps
                18.0              // exam Ws
        );
        GradeService.StudentGpaResponse gpaResponse =
                new GradeService.StudentGpaResponse(List.of(sg), 80.0);

        when(gradeService.getMyGpa("studentId"))
                .thenReturn(gpaResponse);

        ResponseEntity<GradeService.StudentGpaResponse> response = controller.getMyGpa(authentication);

        assertEquals(80.0, response.getBody().getStudentGpa());
        assertThat(response.getBody().getSubjects()).hasSize(1);
        assertEquals("Math", response.getBody().getSubjects().get(0).getSubjectName());
    }

    @Test
    void gradeResponse_shouldSetAllFieldsCorrectly() {
        // Use the existing constructor with the expanded fields
        List<Integer> perfList = List.of(10);
        List<Integer> quizList = List.of(20, 15);
        List<Integer> examList = List.of(18);

        GradeController.GradeResponse response = new GradeController.GradeResponse(
                // identifiers
                1L,
                95.5,                       // initialGrade
                "PASSED",                   // finalStatus

                // student
                "student123",
                "Alice",
                "Smith",

                // roster
                100L,
                "Mathematics",
                "First Period",
                "Algebra Basics",
                "John",
                "Doe",

                // performance breakdown
                perfList,
                10,
                100.0,
                40.0,

                // quiz breakdown
                quizList,
                35,
                56.45,
                22.58,

                // exam breakdown
                examList,
                18,
                90.0,
                18.0
        );

        // Spot‐check a few fields
        assertEquals(1L, response.getGradeId());
        assertEquals(95.5, response.getInitialGrade());
        assertEquals("PASSED", response.getFinalStatus());
        assertEquals("student123", response.getStudentId());
        assertEquals("Alice", response.getStudentFirstName());
        assertEquals("Smith", response.getStudentLastName());
        assertEquals(100L, response.getRosterId());
        assertEquals("Mathematics", response.getSubjectName());
        assertEquals("Algebra Basics", response.getNickname());
        assertEquals("John", response.getTeacherFirstName());

        // Check that the score lists were set correctly
        assertThat(response.getPerformanceScores()).containsExactly(10);
        assertEquals(10, response.getPerformanceTotal());
        assertEquals(100.0, response.getPerformancePs());
        assertEquals(40.0, response.getPerformanceWs());

        assertThat(response.getQuizScores()).containsExactly(20, 15);
        assertEquals(35, response.getQuizTotal());
        assertEquals(56.45, response.getQuizPs());
        assertEquals(22.58, response.getQuizWs());

        assertThat(response.getQuarterlyExamScores()).containsExactly(18);
        assertEquals(18, response.getQuarterlyExamTotal());
        assertEquals(90.0, response.getQuarterlyExamPs());
        assertEquals(18.0, response.getQuarterlyExamWs());
    }

    // Helper to build a simple Grade for tests
    private Grade createMockGrade() {
        // Teacher
        User teacher = new User();
        var teacherProfile = new com.school.roster.school_roster_backend.entity.NonStudentProfile();
        teacherProfile.setFirstName("John");
        teacherProfile.setLastName("Doe");
        teacher.setNonStudentProfile(teacherProfile);

        // Roster
        Roster roster = new Roster();
        roster.setId(10L);
        roster.setSubjectName("Math");
        roster.setPeriod("First");
        roster.setNickname("Algebra");
        roster.setTeacher(teacher);

        // Student
        User student = new User();
        student.setId("studentId");
        var studentProfile = new com.school.roster.school_roster_backend.entity.StudentProfile();
        studentProfile.setFirstName("Alice");
        studentProfile.setLastName("Smith");
        student.setStudentProfile(studentProfile);

        // Build ScoreDetails
        ScoreDetails details = new ScoreDetails();
        details.getPerformanceScores().add(9);
        details.setPerformanceTotal(9);
        details.setPerformancePs(90.0);
        details.setPerformanceWs(36.0);

        details.getQuizScores().add(8);
        details.getQuizScores().add(7);
        details.setQuizTotal(15);
        details.setQuizPs(24.19);
        details.setQuizWs(9.68);

        details.getQuarterlyExamScores().add(18);
        details.setQuarterlyExamTotal(18);
        details.setQuarterlyExamPs(90.0);
        details.setQuarterlyExamWs(18.0);

        // Grade
        Grade grade = new Grade();
        grade.setId(1L);
        grade.setStudent(student);
        grade.setRoster(roster);
        grade.setScoreDetails(details);
        grade.setInitialGrade(90.0);
        grade.setFinalStatus(StudentGradeStatus.PASSED);

        return grade;
    }

    @Test
    void updateGrade_shouldThrowAccessDeniedIfCannotUpdate() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        // Simulate no access
        when(rosterService.canEditRoster(anyLong(), eq(user))).thenReturn(false);

        GradeController.UpdateGradeRequest request = new GradeController.UpdateGradeRequest(
                1L,                         // rosterId
                1L,                         // gradeId
                List.of(90),                // performanceScores
                List.of(85),                // quizScores
                List.of(88)                 // quarterlyExamScores
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.updateGrade(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to update grades in this roster.", ex.getMessage());
    }

    @Test
    void deleteGrade_shouldThrowAccessDeniedIfCannotDelete() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(gradeService.canDeleteGrade(1L, user)).thenReturn(false);

        GradeController.GradeIdRequest request = new GradeController.GradeIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.deleteGrade(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to delete this grade.", ex.getMessage());
    }

    @Test
    void getGradeById_shouldThrowAccessDeniedIfCannotView() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(gradeService.canViewGrade(1L, user)).thenReturn(false);

        GradeController.GradeIdRequest request = new GradeController.GradeIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradeById(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to view this grade.", ex.getMessage());
    }

    @Test
    void getGradesByRoster_shouldThrowAccessDeniedIfCannotViewRoster() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));
        when(rosterService.canViewRoster(1L, user)).thenReturn(false);

        GradeController.RosterIdRequest request = new GradeController.RosterIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradesByRoster(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to view grades in this roster.", ex.getMessage());
    }

    @Test
    void getGradesByStudent_shouldThrowAccessDeniedIfCannotViewStudentGrades() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com"))
                .thenReturn(Optional.of(user));

        // Simulate no roles that allow viewing
        when(userService.hasAnyRole(user, "ADMIN", "ADMINISTRATOR", "TEACHER", "TEACHER_LEAD"))
                .thenReturn(false);

        // And user id ≠ requested student
        GradeController.StudentIdRequest request = new GradeController.StudentIdRequest("student123");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradesByStudent(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to view grades of this student.", ex.getMessage());
    }
}
