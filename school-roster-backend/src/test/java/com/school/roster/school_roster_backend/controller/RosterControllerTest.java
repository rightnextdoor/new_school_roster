package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.*;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import com.school.roster.school_roster_backend.entity.enums.StudentGradeStatus;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class RosterControllerTest {

    @Mock
    private RosterService rosterService;
    @Mock
    private UserService userService;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private RosterController controller;

    @BeforeEach
    void setUp() {
        lenient().when(authentication.getName()).thenReturn("teacher@example.com");
    }

    @Test
    void createRoster_shouldCreateRoster() {
        User teacher = new User();
        teacher.setId("teacherId");

        when(userService.getUserByEmail("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(rosterService.createRoster(any(Roster.class), eq("teacherId")))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Roster incomingRoster = new Roster();
        incomingRoster.setSubjectName("Math");
        incomingRoster.setPeriod("First");
        incomingRoster.setNickname("Algebra");

        ResponseEntity<Roster> response = controller.createRoster(incomingRoster, authentication);

        assertNotNull(response.getBody());
        assertEquals("Math", response.getBody().getSubjectName());
        assertEquals("First", response.getBody().getPeriod());
        assertEquals("Algebra", response.getBody().getNickname());
    }

    @Test
    void updateRoster_shouldUpdateSuccessfully() {
        User teacher = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(anyLong(), eq(teacher))).thenReturn(true);

        Roster roster = new Roster();
        when(rosterService.updateRoster(anyLong(), any())).thenReturn(roster);

        RosterController.UpdateRosterRequest request =
                new RosterController.UpdateRosterRequest(1L, new Roster());
        Roster result = controller.updateRoster(request, authentication).getBody();

        assertEquals(roster, result);
    }

    @Test
    void deleteRoster_shouldDeleteSuccessfully() {
        User teacher = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(anyLong(), eq(teacher))).thenReturn(true);

        RosterController.IdRequest request = new RosterController.IdRequest(1L);
        ResponseEntity<String> response = controller.deleteRoster(request, authentication);

        verify(rosterService).deleteRoster(1L);
        assertEquals("Roster deleted successfully.", response.getBody());
    }

    @Test
    void addStudentToRoster_shouldAddSuccessfully() {
        // Arrange
        User teacher = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(anyLong(), eq(teacher))).thenReturn(true);

        Roster roster = new Roster();
        when(rosterService.addStudentToRoster(anyLong(), anyList())).thenReturn(roster);

        List<String> userIds = Collections.singletonList("studentId");
        RosterController.AddStudentRequest request =
                new RosterController.AddStudentRequest(1L, userIds);

        // Act
        Roster result = controller.addStudentToRoster(request, authentication).getBody();

        // Assert
        assertEquals(roster, result);
    }

    @Test
    void removeStudentFromRoster_shouldRemoveSuccessfully() {
        // Arrange
        User teacher = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(anyLong(), eq(teacher))).thenReturn(true);

        Roster roster = new Roster();
        when(rosterService.removeStudentFromRoster(anyLong(), anyList())).thenReturn(roster);

        List<String> userIds = Collections.singletonList("studentId");
        RosterController.AddStudentRequest request =
                new RosterController.AddStudentRequest(1L, userIds);

        // Act
        Roster result = controller.removeStudentFromRoster(request, authentication).getBody();

        // Assert
        assertEquals(roster, result);
    }


    @Test
    void reassignTeacher_shouldReassignSuccessfully() {
        Roster roster = new Roster();
        when(rosterService.reassignTeacher(anyLong(), anyString())).thenReturn(roster);

        RosterController.ReassignTeacherRequest request =
                new RosterController.ReassignTeacherRequest(1L, "newTeacherId");
        Roster result = controller.reassignTeacher(request).getBody();

        assertEquals(roster, result);
    }

    @Test
    void getRosterById_shouldReturnRosterResponse() {
        User teacher = new User();
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));
        when(rosterService.canViewRoster(anyLong(), eq(teacher))).thenReturn(true);

        Roster roster = new Roster();
        roster.setId(1L);
        when(rosterService.getRosterById(1L)).thenReturn(roster);

        RosterController.IdRequest request = new RosterController.IdRequest(1L);
        RosterController.RosterResponse result =
                controller.getRosterById(request, authentication).getBody();

        assertEquals(1L, result.getRosterId());
    }

    @Test
    void getRostersByStudent_shouldReturnRosterResponses() {
        User student = new User();
        student.setId("studentId");
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(student));

        Roster roster = new Roster();
        roster.setId(2L);
        when(rosterService.getRostersByStudentId("studentId"))
                .thenReturn(List.of(roster));

        List<RosterController.RosterResponse> result =
                controller.getRostersByStudent(authentication).getBody();

        assertEquals(1, result.size());
        assertEquals(2L, result.get(0).getRosterId());
    }

    @Test
    void getRostersByTeacher_shouldReturnRosterResponses() {
        User teacher = new User();
        teacher.setId("teacherId");
        when(userService.getUserByEmail(anyString())).thenReturn(Optional.of(teacher));

        Roster roster = new Roster();
        roster.setId(3L);
        when(rosterService.getRostersByTeacherId("teacherId"))
                .thenReturn(List.of(roster));

        List<RosterController.RosterResponse> result =
                controller.getRostersByTeacher(authentication).getBody();

        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).getRosterId());
    }

    @Test
    void getAllRosters_shouldReturnRosterResponses() {
        Roster roster = new Roster();
        roster.setId(4L);
        when(rosterService.getAllRosters()).thenReturn(List.of(roster));

        List<RosterController.RosterResponse> result =
                controller.getAllRosters().getBody();

        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getRosterId());
    }

    @Test
    void buildRosterResponse_shouldReturnFullyMappedResponse() {
        // Teacher profile
        NonStudentProfile teacherProfile = new NonStudentProfile();
        teacherProfile.setFirstName("TeacherFirst");
        teacherProfile.setLastName("TeacherLast");

        User teacher = new User();
        teacher.setId("teacherId");
        teacher.setNonStudentProfile(teacherProfile);

        // Student profile
        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setFirstName("StudentFirst");
        studentProfile.setLastName("StudentLast");

        User student = new User();
        student.setId("studentId");
        student.setStudentProfile(studentProfile);

        // Create a Grade. Now we set initialGrade (instead of finalGpa).
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setInitialGrade(95.0);
        grade.setFinalStatus(StudentGradeStatus.WITH_HONORS);
        grade.setScoreDetails(new ScoreDetails()); // ensure non-null details

        // Roster with one student and one grade
        Roster roster = new Roster();
        roster.setId(1L);
        roster.setSubjectName("Math");
        roster.setPeriod("First Period");
        roster.setNickname("Algebra Fun");
        roster.setGradeLevel("1");
        roster.setTeacher(teacher);
        roster.setStudents(List.of(student));
        roster.setGrades(List.of(grade));
        roster.setClassGpa(92f);

        RosterController.RosterResponse response =
                controller.buildRosterResponse(roster);

        assertThat(response.getRosterId()).isEqualTo(1L);
        assertThat(response.getSubjectName()).isEqualTo("Math");
        assertThat(response.getPeriod()).isEqualTo("First Period");
        assertThat(response.getNickname()).isEqualTo("Algebra Fun");
        assertThat(response.getGradeLevel()).isEqualTo("1");
        assertThat(response.getTeacherFirstName()).isEqualTo("TeacherFirst");
        assertThat(response.getTeacherLastName()).isEqualTo("TeacherLast");
        assertThat(response.getClassGpa()).isEqualTo(92f);

        assertThat(response.getStudents()).hasSize(1);
        RosterController.StudentInfo studentInfo = response.getStudents().get(0);
        assertThat(studentInfo.getStudentId()).isEqualTo("studentId");
        assertThat(studentInfo.getFirstName()).isEqualTo("StudentFirst");
        assertThat(studentInfo.getLastName()).isEqualTo("StudentLast");
        // Check initialGrade field instead of finalGpa
        assertThat(studentInfo.getFinalGpa()).isEqualTo(95.0);
        assertThat(studentInfo.getFinalStatus()).isEqualTo("WITH_HONORS");
    }


    @Test
    void updateRoster_shouldThrowIfUserCannotEdit() {
        User teacher = new User();
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(eq(1L), eq(teacher))).thenReturn(false);

        RosterController.UpdateRosterRequest request =
                new RosterController.UpdateRosterRequest(1L, new Roster());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.updateRoster(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to update this roster.", ex.getMessage());
    }

    @Test
    void deleteRoster_shouldThrowIfUserCannotEdit() {
        User teacher = new User();
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(eq(1L), eq(teacher))).thenReturn(false);

        RosterController.IdRequest request = new RosterController.IdRequest(1L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.deleteRoster(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to delete this roster.", ex.getMessage());
    }

    @Test
    void addStudent_shouldThrowIfUserCannotEdit() {
        User teacher = new User();
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(eq(1L), eq(teacher))).thenReturn(false);
        List<String> userId = new ArrayList<>();
        userId.add("studentId");
        RosterController.AddStudentRequest request =
                new RosterController.AddStudentRequest(1L, userId);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.addStudentToRoster(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to add students to this roster.", ex.getMessage());
    }

    @Test
    void removeStudent_shouldThrowIfUserCannotEdit() {
        User teacher = new User();
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com"))
                .thenReturn(Optional.of(teacher));
        when(rosterService.canEditRoster(eq(1L), eq(teacher))).thenReturn(false);
        List<String> userId = new ArrayList<>();
        userId.add("studentId");
        RosterController.AddStudentRequest request =
                new RosterController.AddStudentRequest(1L, userId);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.removeStudentFromRoster(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to remove students from this roster.", ex.getMessage());
    }

    @Test
    void getRosterById_shouldThrowIfUserCannotView() {
        User user = new User();
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com"))
                .thenReturn(Optional.of(user));
        when(rosterService.canViewRoster(eq(1L), eq(user))).thenReturn(false);

        RosterController.IdRequest request = new RosterController.IdRequest(1L);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.getRosterById(request, authentication)
        );
        assertEquals("Access denied: You are not allowed to view this roster.", ex.getMessage());
    }

    @Test
    void updateRosterRequest_shouldReturnUpdatedRoster() {
        Roster roster = new Roster();
        RosterController.UpdateRosterRequest request =
                new RosterController.UpdateRosterRequest(1L, roster);

        assertEquals(roster, request.getRosterData());
    }
}
