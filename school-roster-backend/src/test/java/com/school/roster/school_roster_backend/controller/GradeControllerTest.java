package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.service.GradeService;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

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
        User user = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(rosterService.canEditRoster(1L, user)).thenReturn(true);

        Grade grade = createMockGrade();
        when(gradeService.updateGrades(any(), any(), any(), any(), any())).thenReturn(grade);

        var request = new GradeController.UpdateGradeRequest("studentId", 1L, List.of(90f), List.of(80f), List.of(85f));
        ResponseEntity<GradeController.GradeResponse> response = controller.updateGrade(request, authentication);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getGradeId());
        assertEquals(90f, response.getBody().getFinalGpa());
        assertEquals("PASSED", response.getBody().getFinalStatus());
        assertEquals("studentId", response.getBody().getStudentId());
        assertEquals("Math", response.getBody().getSubjectName());
    }

    @Test
    void deleteGrade_shouldDeleteGradeSuccessfully() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(gradeService.canDeleteGrade(1L, user)).thenReturn(true);

        ResponseEntity<String> response = controller.deleteGrade(new GradeController.GradeIdRequest(1L), authentication);

        verify(gradeService).deleteGrade(1L);
        assertEquals("Grade deleted successfully.", response.getBody());
    }

    @Test
    void getGradeById_shouldReturnGradeResponse() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(gradeService.canViewGrade(1L, user)).thenReturn(true);

        Grade grade = createMockGrade();
        when(gradeService.getGradeById(1L)).thenReturn(grade);

        ResponseEntity<GradeController.GradeResponse> response = controller.getGradeById(new GradeController.GradeIdRequest(1L), authentication);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getGradeId());
        assertEquals("Math", response.getBody().getSubjectName());
        assertEquals("John", response.getBody().getTeacherFirstName());
    }

    @Test
    void getGradesByRoster_shouldReturnListOfGradeResponses() {
        User user = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(rosterService.canViewRoster(1L, user)).thenReturn(true);

        Grade grade = createMockGrade();
        when(gradeService.getGradesByRosterId(1L)).thenReturn(List.of(grade));

        ResponseEntity<List<GradeController.GradeResponse>> response = controller.getGradesByRoster(
                new GradeController.RosterIdRequest(1L), authentication);

        assertThat(response.getBody()).hasSize(1);
        assertEquals("Math", response.getBody().get(0).getSubjectName());
    }

    @Test
    void getGradesByStudent_shouldReturnListOfGradeResponses() {
        User user = new User();
        user.setId("studentId");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        Grade grade = createMockGrade();
        when(gradeService.getGradesByStudentId("studentId")).thenReturn(List.of(grade));

        ResponseEntity<List<GradeController.GradeResponse>> response = controller.getGradesByStudent(
                new GradeController.StudentIdRequest("studentId"), authentication);

        assertThat(response.getBody()).hasSize(1);
        assertEquals(90f, response.getBody().get(0).getFinalGpa());
    }

    @Test
    void getMyGpa_shouldReturnStudentGpa() {
        User user = new User();
        user.setId("studentId");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        GradeService.StudentGpaResponse gpaResponse = new GradeService.StudentGpaResponse(List.of(), 90f);
        when(gradeService.getMyGpa("studentId")).thenReturn(gpaResponse);

        ResponseEntity<GradeService.StudentGpaResponse> response = controller.getMyGpa(authentication);

        assertEquals(90f, response.getBody().getStudentGpa());
    }

    @Test
    void gradeResponse_shouldSetAllFieldsCorrectly() {
        GradeController.GradeResponse response = new GradeController.GradeResponse(
                1L,
                95.5f,
                "PASSED",
                "student123",
                "Alice",
                "Smith",
                100L,
                "Mathematics",
                "First Period",
                "Algebra Basics",
                "John",
                "Doe"
        );

        assertEquals(1L, response.getGradeId());
        assertEquals(95.5f, response.getFinalGpa());
        assertEquals("PASSED", response.getFinalStatus());
        assertEquals("student123", response.getStudentId());
        assertEquals("Alice", response.getStudentFirstName());
        assertEquals("Smith", response.getStudentLastName());
        assertEquals(100L, response.getRosterId());
        assertEquals("Mathematics", response.getSubjectName());
        assertEquals("First Period", response.getPeriod());
        assertEquals("Algebra Basics", response.getNickname());
        assertEquals("John", response.getTeacherFirstName());
        assertEquals("Doe", response.getTeacherLastName());
    }


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

        // Grade
        return Grade.builder()
                .id(1L)
                .student(student)
                .roster(roster)
                .finalGpa(90f)
                .finalStatus(StudentGradeStatus.PASSED)
                .build();
    }

    @Test
    void updateGrade_shouldThrowAccessDeniedIfCannotUpdate() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        // Important: simulate no access
        when(gradeService.canUpdateGrade(anyLong(), eq(user))).thenReturn(false);

        GradeController.UpdateGradeRequest request = new GradeController.UpdateGradeRequest(
                "studentId",
                1L,
                List.of(90f), // performanceScores
                List.of(85f), // quizScores
                List.of(88f)  // quarterlyExamScores
        );

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.updateGrade(request, authentication));

        assertEquals("Access denied: You are not allowed to update grades in this roster.", ex.getMessage());
    }


    @Test
    void deleteGrade_shouldThrowAccessDeniedIfCannotDelete() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(gradeService.canDeleteGrade(1L, user)).thenReturn(false);

        GradeController.GradeIdRequest request = new GradeController.GradeIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.deleteGrade(request, authentication));
        assertEquals("Access denied: You are not allowed to delete this grade.", ex.getMessage());
    }

    @Test
    void getGradeById_shouldThrowAccessDeniedIfCannotView() {
        when(authentication.getName()).thenReturn("user@example.com");
        User user = new User();
        user.setId("u1");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(gradeService.canViewGrade(1L, user)).thenReturn(false);

        GradeController.GradeIdRequest request = new GradeController.GradeIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradeById(request, authentication));
        assertEquals("Access denied: You are not allowed to view this grade.", ex.getMessage());
    }

    @Test
    void getGradesByRoster_shouldThrowAccessDeniedIfCannotViewRoster() {
        when(authentication.getName()).thenReturn("user@example.com");

        User user = new User();
        user.setId("u1");

        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(rosterService.canViewRoster(1L, user)).thenReturn(false);

        GradeController.RosterIdRequest request = new GradeController.RosterIdRequest(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradesByRoster(request, authentication));
        assertEquals("Access denied: You are not allowed to view grades in this roster.", ex.getMessage());
    }

    @Test
    void getGradesByStudent_shouldThrowAccessDeniedIfCannotViewStudentGrades() {
        when(authentication.getName()).thenReturn("user@example.com");

        User user = new User();
        user.setId("u1");

        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasRole(user, "STUDENT")).thenReturn(false);
        when(userService.hasRole(user, "ADMIN")).thenReturn(false);
        when(userService.hasRole(user, "ADMINISTRATOR")).thenReturn(false);

        GradeController.StudentIdRequest request = new GradeController.StudentIdRequest("student123");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.getGradesByStudent(request, authentication));
        assertEquals("Access denied: You are not allowed to view grades of this student.", ex.getMessage());
    }

}
