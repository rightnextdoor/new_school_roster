package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherLeadServiceTest {

    private UserRepository userRepository;
    private TeacherLeadService teacherLeadService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        teacherLeadService = new TeacherLeadService(userRepository);
    }

    @Test
    void addTeacher_shouldAddTeacherToLead() {
        User lead = createLeadUser("lead123");
        User teacher = createTeacherUser("teacher456");
        User caller = lead;

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        teacherLeadService.addTeacher(lead.getId(), teacher.getId(), caller);

        assertTrue(lead.getAssignedTeachers().contains(teacher));
        verify(userRepository, times(1)).save(lead);
    }

    @Test
    void addTeacher_shouldFailWhenCallerIsNotAllowed() {
        User lead = createLeadUser("lead123");
        User teacher = createTeacherUser("teacher456");
        User caller = createTeacherUser("badCaller");

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(userRepository.findById(teacher.getId())).thenReturn(Optional.of(teacher));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                teacherLeadService.addTeacher(lead.getId(), teacher.getId(), caller));

        assertEquals("Access denied: cannot manage this teacher lead.", ex.getMessage());
    }

    @Test
    void removeTeacher_shouldRemoveTeacherFromLead() {
        User lead = createLeadUser("lead123");
        User teacher = createTeacherUser("teacher456");
        lead.getAssignedTeachers().add(teacher);
        User caller = lead;

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        teacherLeadService.removeTeacher(lead.getId(), teacher.getId(), caller);

        assertFalse(lead.getAssignedTeachers().contains(teacher));
        verify(userRepository, times(1)).save(lead);
    }

    @Test
    void removeTeacher_shouldFailWhenCallerIsNotAllowed() {
        User lead = createLeadUser("lead123");
        User caller = createTeacherUser("badCaller");

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                teacherLeadService.removeTeacher(lead.getId(), "teacher456", caller));

        assertEquals("Access denied: cannot manage this teacher lead.", ex.getMessage());
    }

    @Test
    void getMyTeachers_shouldReturnAssignedTeachers() {
        User lead = createLeadUser("lead123");
        lead.getAssignedTeachers().add(createTeacherUser("teacher456"));

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));

        List<User> teachers = teacherLeadService.getMyTeachers(lead);

        assertEquals(1, teachers.size());
        assertEquals("teacher456", teachers.get(0).getId());
    }

    @Test
    void getTeachersForAdmin_shouldReturnAssignedTeachers() {
        User lead = createLeadUser("lead123");
        lead.getAssignedTeachers().add(createTeacherUser("teacher789"));

        when(userRepository.findById(lead.getId())).thenReturn(Optional.of(lead));

        List<User> teachers = teacherLeadService.getTeachersForAdmin(lead.getId());

        assertEquals(1, teachers.size());
        assertEquals("teacher789", teachers.get(0).getId());
    }

    // === Helper methods ===

    private User createLeadUser(String id) {
        return User.builder()
                .id(id)
                .roles(Set.of(Role.TEACHER_LEAD))
                .assignedTeachers(new java.util.ArrayList<>())
                .build();
    }

    private User createTeacherUser(String id) {
        return User.builder()
                .id(id)
                .roles(Set.of(Role.TEACHER))
                .build();
    }
}
