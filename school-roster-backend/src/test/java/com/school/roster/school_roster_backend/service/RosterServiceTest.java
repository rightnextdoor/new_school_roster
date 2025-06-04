package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RosterServiceTest {

    private RosterRepository rosterRepository;
    private UserRepository userRepository;
    private GradeRepository gradeRepository;
    private GradeService gradeService;
    private HighestPossibleScoreService highestPossibleScoreService;
    private RosterService rosterService;

    @BeforeEach
    void setUp() {
        rosterRepository = mock(RosterRepository.class);
        userRepository = mock(UserRepository.class);
        gradeRepository = mock(GradeRepository.class);
        gradeService = mock(GradeService.class);
        highestPossibleScoreService = mock(HighestPossibleScoreService.class);

        // Construct the service under test
        rosterService = new RosterService(
                rosterRepository,
                userRepository,
                gradeRepository,
                gradeService,
                highestPossibleScoreService
        );

        // By default, stub save(...) to return its argument
        when(rosterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createRoster_shouldAssignTeacherAndDefaults() {
        User teacher = new User();
        when(userRepository.findById("teacherId")).thenReturn(Optional.of(teacher));

        Roster inputRoster = new Roster();
        inputRoster.setSubjectName("Science");
        inputRoster.setPeriod("2nd Period");
        inputRoster.setNickname("Intro to Science");

        // Act
        Roster saved = rosterService.createRoster(inputRoster, "teacherId");

        // Assert
        assertEquals(teacher, saved.getTeacher());
        assertEquals(0f, saved.getClassGpa());
        assertNotNull(saved.getStudents(), "students list should be initialized");
        assertTrue(saved.getStudents().isEmpty());
        assertNotNull(saved.getGrades(), "grades list should be initialized");
        assertTrue(saved.getGrades().isEmpty());
        assertEquals("Science", saved.getSubjectName());
        assertEquals("2nd Period", saved.getPeriod());
        assertEquals("Intro to Science", saved.getNickname());
    }

    @Test
    void createRoster_teacherNotFound_shouldThrow() {
        when(userRepository.findById("badId")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> rosterService.createRoster(new Roster(), "badId")
        );

        assertEquals("Teacher not found with ID: badId", ex.getMessage());
    }

    @Test
    void addStudentToRoster_shouldAddNewStudentAndGrade() {
        // Prepare a Roster with empty lists
        Roster roster = new Roster();
        roster.setStudents(new ArrayList<>());
        roster.setGrades(new ArrayList<>());
        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));

        // Prepare a User as the student
        User student = new User();
        student.setId("studentId");
        when(userRepository.findById("studentId")).thenReturn(Optional.of(student));

        // Stub gradeService.createGrade(rosterId, studentId) to return a Grade whose student is set
        Grade fakeGrade = new Grade();
        fakeGrade.setStudent(student);
        when(gradeService.createGrade(roster, student)).thenReturn(fakeGrade);
        List<String> userId = new ArrayList<>();
        userId.add("studentId");
        // Act
        Roster updated = rosterService.addStudentToRoster(1L, userId);

        // Assert: the student was added to roster.getStudents()
        assertThat(updated.getStudents()).containsExactly(student);

        // Assert: the gradeService.createGrade(...) was invoked, and that the returned Grade was appended
        assertThat(updated.getGrades()).hasSize(1);
        assertEquals(student, updated.getGrades().get(0).getStudent());
    }

    @Test
    void addStudentToRoster_rosterOrStudentNotFound_shouldThrow() {
        // Case 1: Roster not found
        when(rosterRepository.findById(1L)).thenReturn(Optional.empty());
        List<String> userId = new ArrayList<>();
        userId.add("studentId");
        assertThrows(RuntimeException.class,
                () -> rosterService.addStudentToRoster(1L, userId),
                "Expected exception when roster not found");

        // Case 2: Roster exists, but student not found
        Roster roster = new Roster();
        roster.setStudents(new ArrayList<>());
        roster.setGrades(new ArrayList<>());
        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));
        when(userRepository.findById("badId")).thenReturn(Optional.empty());
        List<String> userId2 = new ArrayList<>();
        userId2.add("badId");
        assertThrows(RuntimeException.class,
                () -> rosterService.addStudentToRoster(1L, userId2),
                "Expected exception when student not found");
    }

    @Test
    void isStudentUnderTeacher_shouldReturnTrueOrFalse() {
        User student = new User();
        student.setId("studentId");

        Roster roster = new Roster();
        roster.setStudents(List.of(student));

        when(rosterRepository.findByTeacherId("teacherId")).thenReturn(List.of(roster));

        assertTrue(rosterService.isStudentUnderTeacher("teacherId", "studentId"));
        assertFalse(rosterService.isStudentUnderTeacher("teacherId", "wrongId"));
    }

    @Test
    void removeStudentFromRoster_shouldRemoveStudentAndDeleteGrade() {
        // Prepare a roster that contains exactly one student
        User student = new User();
        student.setId("studentId");

        Roster roster = new Roster();
        roster.setId(1L);  // <— FIX: set the ID here
        roster.setStudents(new ArrayList<>(List.of(student)));
        roster.setGrades(new ArrayList<>());

        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));
        when(userRepository.findById("studentId")).thenReturn(Optional.of(student));

        // Stub gradeRepository.findByRosterId so that no matching grades exist
        when(gradeRepository.findByRosterId(1L)).thenReturn(new ArrayList<>());
        List<String> userId = new ArrayList<>();
        userId.add("studentId");
        // Act
        rosterService.removeStudentFromRoster(1L, userId);

        // Assert: student removed
        assertThat(roster.getStudents()).isEmpty();

        // Verify that findByRosterId was called with 1L (not null)
        verify(gradeRepository).findByRosterId(1L);
    }

    @Test
    void updateRoster_shouldUpdateRosterFields() {
        Roster roster = new Roster();
        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));

        Roster updateData = new Roster();
        updateData.setSubjectName("Math");
        updateData.setPeriod("1st");
        updateData.setNickname("Algebra");

        Roster updated = rosterService.updateRoster(1L, updateData);

        assertEquals("Math", updated.getSubjectName());
        assertEquals("1st", updated.getPeriod());
        assertEquals("Algebra", updated.getNickname());
    }

    @Test
    void reassignTeacher_shouldUpdateTeacher() {
        Roster roster = new Roster();
        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));

        User newTeacher = new User();
        when(userRepository.findById("newId")).thenReturn(Optional.of(newTeacher));

        Roster updated = rosterService.reassignTeacher(1L, "newId");

        assertEquals(newTeacher, updated.getTeacher());
    }

    @Test
    void deleteRoster_shouldDeleteRosterAndGrades() {
        Roster roster = new Roster();
        roster.setStudents(new ArrayList<>());
        roster.setGrades(new ArrayList<>());

        when(rosterRepository.findById(1L)).thenReturn(Optional.of(roster));
        when(gradeRepository.findByRosterId(1L)).thenReturn(new ArrayList<>());

        // Act
        rosterService.deleteRoster(1L);

        // Assert: both deleteAll(grades) and delete(roster) should be invoked
        verify(gradeRepository).deleteAll(Collections.emptyList());
        verify(rosterRepository).delete(roster);
    }

    @Test
    void getRosterById_shouldReturnRoster() {
        Roster roster = new Roster();
        when(rosterRepository.findByIdWithTeacherAndStudents(1L))
                .thenReturn(Optional.of(roster));

        Roster result = rosterService.getRosterById(1L);

        assertEquals(roster, result);
    }

    @Test
    void getRostersByStudentId_shouldReturnCorrectRosters() {
        User student = new User();
        student.setId("studentId");

        Roster roster = new Roster();
        roster.setStudents(List.of(student));

        when(rosterRepository.findAll()).thenReturn(List.of(roster));

        List<Roster> result = rosterService.getRostersByStudentId("studentId");

        assertThat(result).hasSize(1);
    }

    @Test
    void canEditRoster_shouldReturnCorrectly() {
        User teacher = new User();
        teacher.setId("teacherId");

        User admin = new User();
        admin.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.ADMIN));

        Roster roster = new Roster();
        roster.setTeacher(teacher);

        when(rosterRepository.findByIdWithTeacherAndStudents(1L))
                .thenReturn(Optional.of(roster));

        assertTrue(rosterService.canEditRoster(1L, teacher));
        assertTrue(rosterService.canEditRoster(1L, admin));
        assertFalse(rosterService.canEditRoster(1L, new User()));
    }

    @Test
    void canViewRoster_shouldReturnCorrectly() {
        User teacher = new User();
        teacher.setId("teacherId");

        User student = new User();
        student.setId("studentId");

        User admin = new User();
        admin.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.ADMINISTRATOR));

        Roster roster = new Roster();
        roster.setTeacher(teacher);
        roster.setStudents(List.of(student));

        when(rosterRepository.findByIdWithTeacherAndStudents(1L))
                .thenReturn(Optional.of(roster));

        assertTrue(rosterService.canViewRoster(1L, teacher));
        assertTrue(rosterService.canViewRoster(1L, student));
        assertTrue(rosterService.canViewRoster(1L, admin));
        assertFalse(rosterService.canViewRoster(1L, new User()));
    }

    @Test
    void getRostersByTeacherId_shouldReturnTeacherRosters() {
        Roster roster1 = new Roster();
        roster1.setId(1L);
        when(rosterRepository.findByTeacherId("teacherId"))
                .thenReturn(List.of(roster1));

        List<Roster> rosters = rosterService.getRostersByTeacherId("teacherId");

        assertEquals(1, rosters.size());
        assertEquals(1L, rosters.get(0).getId());
    }

    @Test
    void getAllRosters_shouldReturnAllRosters() {
        Roster roster1 = new Roster();
        roster1.setId(2L);
        Roster roster2 = new Roster();
        roster2.setId(3L);
        when(rosterRepository.findAll()).thenReturn(List.of(roster1, roster2));

        List<Roster> rosters = rosterService.getAllRosters();

        assertEquals(2, rosters.size());
    }

    @Test
    void canEditRoster_shouldReturnTrueForAdminAdministratorTeacherLead() {
        // Prepare Roster whose teacher is "teacherId"
        Roster roster = new Roster();
        User teacher = new User();
        teacher.setId("teacherId");
        roster.setTeacher(teacher);

        when(rosterRepository.findByIdWithTeacherAndStudents(anyLong()))
                .thenReturn(Optional.of(roster));

        // User with ADMIN role
        User admin = new User();
        admin.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.ADMIN));
        assertTrue(rosterService.canEditRoster(1L, admin));

        // User with ADMINISTRATOR role
        User administrator = new User();
        administrator.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.ADMINISTRATOR));
        assertTrue(rosterService.canEditRoster(1L, administrator));

        // User with TEACHER_LEAD role
        User teacherLead = new User();
        teacherLead.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.TEACHER_LEAD));
        assertTrue(rosterService.canEditRoster(1L, teacherLead));
    }

    @Test
    void canViewRoster_shouldReturnTrueForOfficeAdministrator() {
        // Prepare Roster whose teacher is "teacherId"
        Roster roster = new Roster();
        User teacher = new User();
        teacher.setId("teacherId");
        roster.setTeacher(teacher);

        when(rosterRepository.findByIdWithTeacherAndStudents(anyLong()))
                .thenReturn(Optional.of(roster));

        // User with OFFICE_ADMINISTRATOR role
        User officeAdmin = new User();
        officeAdmin.setRoles(Set.of(com.school.roster.school_roster_backend.entity.enums.Role.OFFICE_ADMINISTRATOR));
        assertTrue(rosterService.canViewRoster(1L, officeAdmin));
    }
}
