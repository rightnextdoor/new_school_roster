package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.*;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private NonStudentProfileRepository nonStudentProfileRepository;
    @Mock
    private RosterRepository rosterRepository;
    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("123456789012")
                .email("test@example.com")
                .password("password")
                .roles(new HashSet<>(Collections.singleton(Role.STUDENT)))
                .build();
    }

    @Test
    void createUser_shouldSaveEncodedPasswordAndGeneratedId() {
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User createdUser = userService.createUser(user);

        assertThat(createdUser.getPassword()).isNotEqualTo("password");
        assertThat(createdUser.getId()).hasSize(12);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> users = userService.getAllUsers();

        assertThat(users).containsExactly(user);
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(user.getId());

        assertThat(result).contains(user);
    }

    @Test
    void getUserByEmail_shouldReturnUser() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserByEmail(user.getEmail());

        assertThat(result).contains(user);
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User updatedData = User.builder()
                .email("new@example.com")
                .password("newPassword")
                .roles(new HashSet<>(Collections.singleton(Role.TEACHER)))
                .build();

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(user.getId(), updatedData);

        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(updatedUser.getRoles()).containsExactly(Role.TEACHER);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void hasRole_shouldReturnTrueWhenRoleExists() {
        assertThat(userService.hasRole(user, "STUDENT")).isTrue();
    }

    @Test
    void hasRole_shouldReturnFalseWhenRoleDoesNotExist() {
        assertThat(userService.hasRole(user, "ADMIN")).isFalse();
    }

    @Test
    void hasAnyRole_shouldReturnTrueIfAnyRoleMatches() {
        assertThat(userService.hasAnyRole(user, "ADMIN", "STUDENT")).isTrue();
    }

    @Test
    void hasAnyRole_shouldReturnFalseIfNoRoleMatches() {
        assertThat(userService.hasAnyRole(user, "ADMIN", "TEACHER")).isFalse();
    }

    @Test
    void deleteUser_shouldDeleteUserWithoutRoster() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(rosterRepository.findAll()).thenReturn(new ArrayList<>());
        when(gradeRepository.findByStudentId(user.getId())).thenReturn(new ArrayList<>());

        userService.deleteUser(user.getId());

        verify(userRepository).deleteById(user.getId());
    }

    @Test
    void deleteUser_shouldThrowWhenTeacherStillHasRoster() {
        User teacher = User.builder()
                .id("987654321098")
                .email("teacher@example.com")
                .roles(new HashSet<>(Collections.singleton(Role.TEACHER)))
                .build();

        Roster roster = Roster.builder()
                .teacher(teacher)
                .subjectName("Math")
                .build();

        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(rosterRepository.findByTeacherId(teacher.getId())).thenReturn(List.of(roster));

        assertThatThrownBy(() -> userService.deleteUser(teacher.getId()))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cannot delete Teacher");
    }

    @Test
    void generateUniqueUserId_shouldGenerate12DigitId() {
        when(userRepository.existsById(anyString())).thenReturn(false);

        String id = userService.generateUniqueUserId();

        assertThat(id).hasSize(12);
    }

    @Test
    void deleteUser_shouldDeleteProfilesAndUnlinkFromRosters() {
        // Arrange
        User user = new User();
        user.setId("studentId");

        StudentProfile studentProfile = new StudentProfile();
        studentProfile.setLinkedUser(user);
        user.setStudentProfile(studentProfile);

        NonStudentProfile nonStudentProfile = new NonStudentProfile();
        nonStudentProfile.setLinkedUser(user);
        user.setNonStudentProfile(nonStudentProfile);

        Roster roster1 = new Roster();
        roster1.setStudents(new ArrayList<>(List.of(user)));
        Roster roster2 = new Roster();
        roster2.setStudents(new ArrayList<>(List.of(user)));

        Grade grade = new Grade();
        grade.setStudent(user);

        // Mocking
        when(userRepository.findById("studentId")).thenReturn(Optional.of(user));
        when(gradeRepository.findByStudentId("studentId")).thenReturn(List.of(grade));
        // Mark this line as lenient
        lenient().when(rosterRepository.findAll()).thenReturn(List.of(roster1, roster2));

        // Act
        userService.deleteUser("studentId");

        // Assert
        // After deleting, roster students should be empty
        assertFalse(roster1.getStudents().contains(user));
        assertFalse(roster2.getStudents().contains(user));

        verify(studentProfileRepository).delete(studentProfile);
        verify(nonStudentProfileRepository).delete(nonStudentProfile);
        verify(gradeRepository).deleteAll(List.of(grade));
        verify(userRepository).deleteById("studentId");
    }

    @Test
    void user_shouldStoreTeachingAndStudentRosters() {
        // Create sample rosters
        Roster teachingRoster = new Roster();
        teachingRoster.setId(1L);
        Roster studentRoster = new Roster();
        studentRoster.setId(2L);

        // Create and configure user
        User user = new User();
        user.setTeachingRosters(List.of(teachingRoster));
        user.setStudentRosters(List.of(studentRoster));

        // Assertions
        assertEquals(1, user.getTeachingRosters().size());
        assertEquals(1L, user.getTeachingRosters().get(0).getId());

        assertEquals(1, user.getStudentRosters().size());
        assertEquals(2L, user.getStudentRosters().get(0).getId());
    }

}
