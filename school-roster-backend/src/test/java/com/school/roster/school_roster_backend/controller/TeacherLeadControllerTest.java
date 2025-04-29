package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.TeacherLeadService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherLeadControllerTest {

    @Mock
    private TeacherLeadService teacherLeadService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private TeacherLeadController controller;

    @Test
    void addTeacher_shouldAddSuccessfully() {
        User caller = new User();
        when(authentication.getName()).thenReturn("lead@example.com");
        when(userService.getUserByEmail("lead@example.com")).thenReturn(Optional.of(caller));

        TeacherLeadController.ManageTeacherRequest request = new TeacherLeadController.ManageTeacherRequest("leadId", "teacherId");
        ResponseEntity<String> response = controller.addTeacher(request, authentication);

        verify(teacherLeadService).addTeacher("leadId", "teacherId", caller);
        assertEquals("Teacher added to lead.", response.getBody());
    }

    @Test
    void removeTeacher_shouldRemoveSuccessfully() {
        User caller = new User();
        when(authentication.getName()).thenReturn("lead@example.com");
        when(userService.getUserByEmail("lead@example.com")).thenReturn(Optional.of(caller));

        TeacherLeadController.ManageTeacherRequest request = new TeacherLeadController.ManageTeacherRequest("leadId", "teacherId");
        ResponseEntity<String> response = controller.removeTeacher(request, authentication);

        verify(teacherLeadService).removeTeacher("leadId", "teacherId", caller);
        assertEquals("Teacher removed from lead.", response.getBody());
    }

    @Test
    void getMyTeachers_shouldReturnTeachers() {
        User caller = new User();
        when(authentication.getName()).thenReturn("lead@example.com");
        when(userService.getUserByEmail("lead@example.com")).thenReturn(Optional.of(caller));

        List<User> teachers = List.of(new User(), new User());
        when(teacherLeadService.getMyTeachers(caller)).thenReturn(teachers);

        ResponseEntity<List<User>> response = controller.getMyTeachers(authentication);

        assertEquals(2, response.getBody().size());
        verify(teacherLeadService).getMyTeachers(caller);
    }

    @Test
    void getTeachersForAdmin_shouldReturnTeachers() {
        List<User> teachers = List.of(new User(), new User());
        when(teacherLeadService.getTeachersForAdmin("leadId")).thenReturn(teachers);

        TeacherLeadController.LeadIdRequest request = new TeacherLeadController.LeadIdRequest("leadId");
        ResponseEntity<List<User>> response = controller.getTeachersForAdmin(request);

        assertEquals(2, response.getBody().size());
        verify(teacherLeadService).getTeachersForAdmin("leadId");
    }
}
