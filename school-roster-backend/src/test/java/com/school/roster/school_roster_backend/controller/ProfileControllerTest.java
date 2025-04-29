package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.service.ProfileService;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileControllerTest {

    private ProfileService profileService;
    private UserService userService;
    private RosterService rosterService;
    private ProfileController controller;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        profileService = mock(ProfileService.class);
        userService = mock(UserService.class);
        rosterService = mock(RosterService.class);
        authentication = mock(Authentication.class);
        controller = new ProfileController(profileService, userService, rosterService);

        when(authentication.getName()).thenReturn("user@example.com");
    }

    @Test
    void createStudentProfile_shouldCreateSuccessfully() {
        User teacher = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(teacher));
        when(userService.hasRole(teacher, "TEACHER")).thenReturn(false);

        StudentProfile studentProfile = new StudentProfile();
        when(profileService.createStudentProfile(any(), any())).thenReturn(studentProfile);

        ProfileController.CreateStudentProfileRequest request = new ProfileController.CreateStudentProfileRequest("studentId", new StudentProfile());
        ResponseEntity<StudentProfile> response = controller.createStudentProfile(request, authentication);

        assertEquals(studentProfile, response.getBody());
    }

    @Test
    void updateStudentProfile_shouldUpdateSuccessfully() {
        User teacher = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(teacher));
        when(userService.hasRole(teacher, "TEACHER")).thenReturn(false);

        StudentProfile updatedProfile = new StudentProfile();
        when(profileService.updateStudentProfile(anyLong(), any())).thenReturn(updatedProfile);

        ProfileController.UpdateStudentProfileRequest request = new ProfileController.UpdateStudentProfileRequest(1L, new StudentProfile());
        ResponseEntity<StudentProfile> response = controller.updateStudentProfile(request, authentication);

        assertEquals(updatedProfile, response.getBody());
    }

    @Test
    void deleteStudentProfile_shouldDeleteSuccessfully() {
        ResponseEntity<String> response = controller.deleteStudentProfile(new ProfileController.IdRequest(1L));

        verify(profileService).deleteStudentProfile(1L);
        assertEquals("Student profile deleted successfully.", response.getBody());
    }

    @Test
    void createNonStudentProfile_shouldCreateSuccessfully() {
        User user = new User();
        user.setId("userId");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasAnyRole(user, "ADMIN", "ADMINISTRATOR", "OFFICE_ADMINISTRATOR")).thenReturn(true);

        NonStudentProfile profile = new NonStudentProfile();
        when(profileService.createNonStudentProfile(any(), any())).thenReturn(profile);

        ProfileController.CreateNonStudentProfileRequest request = new ProfileController.CreateNonStudentProfileRequest("userId", new NonStudentProfile());
        ResponseEntity<NonStudentProfile> response = controller.createNonStudentProfile(authentication, request);

        assertEquals(profile, response.getBody());
    }

    @Test
    void updateNonStudentProfile_shouldUpdateSuccessfully() {
        User user = new User();
        user.setId("userId");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasAnyRole(any(), eq("ADMIN"), eq("ADMINISTRATOR"))).thenReturn(true);

        NonStudentProfile updatedProfile = new NonStudentProfile();
        when(profileService.updateNonStudentProfile(anyLong(), any())).thenReturn(updatedProfile);

        ProfileController.UpdateNonStudentProfileRequest request = new ProfileController.UpdateNonStudentProfileRequest(1L, new NonStudentProfile());
        ResponseEntity<NonStudentProfile> response = controller.updateNonStudentProfile(request, authentication);

        assertEquals(updatedProfile, response.getBody());
    }

    @Test
    void deleteNonStudentProfile_shouldDeleteSuccessfully() {
        ResponseEntity<String> response = controller.deleteNonStudentProfile(new ProfileController.IdRequest(1L));

        verify(profileService).deleteNonStudentProfile(1L);
        assertEquals("Non-student profile deleted successfully.", response.getBody());
    }

    @Test
    void getStudentProfileById_shouldReturnProfile() {
        StudentProfile profile = new StudentProfile();
        when(profileService.getStudentProfileById(1L)).thenReturn(profile);

        ResponseEntity<StudentProfile> response = controller.getStudentProfileById(new ProfileController.IdRequest(1L));

        assertEquals(profile, response.getBody());
    }

    @Test
    void getNonStudentProfileById_shouldReturnProfile() {
        NonStudentProfile profile = new NonStudentProfile();
        when(profileService.getNonStudentProfileById(1L)).thenReturn(profile);

        ResponseEntity<NonStudentProfile> response = controller.getNonStudentProfileById(new ProfileController.IdRequest(1L));

        assertEquals(profile, response.getBody());
    }

    @Test
    void getProfileByUserId_shouldReturnProfile() {
        StudentProfile profile = new StudentProfile();
        when(profileService.getProfileByUserId("userId")).thenReturn(profile);

        ResponseEntity<Object> response = controller.getProfileByUserId(new ProfileController.UserIdRequest("userId"));

        assertEquals(profile, response.getBody());
    }

    @Test
    void getAllProfiles_shouldReturnList() {
        List<Object> profiles = List.of(new StudentProfile(), new NonStudentProfile());
        when(profileService.getAllProfiles()).thenReturn(profiles);

        ResponseEntity<List<Object>> response = controller.getAllProfiles();

        assertEquals(2, response.getBody().size());
    }

    @Test
    void getMyProfile_shouldReturnProfile() {
        User user = new User();
        user.setId("userId");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        StudentProfile profile = new StudentProfile();
        when(profileService.getProfileByUserId("userId")).thenReturn(profile);

        ResponseEntity<Object> response = controller.getMyProfile(authentication);

        assertEquals(profile, response.getBody());
    }

    @Test
    void createStudentProfile_shouldDenyIfStudentNotUnderTeacher() {
        User teacher = new User();
        teacher.setId("t1");
        teacher.setRoles(Set.of(Role.TEACHER));
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(userService.hasRole(teacher, "TEACHER")).thenReturn(true);
        when(rosterService.isStudentUnderTeacher("t1", "stu123")).thenReturn(false);

        var request = new ProfileController.CreateStudentProfileRequest("stu123", new StudentProfile());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.createStudentProfile(request, authentication));
        assertEquals("Access denied: Student is not under your roster.", ex.getMessage());
    }

    @Test
    void updateStudentProfile_shouldDenyIfStudentNotUnderTeacher() {
        User teacher = new User();
        teacher.setId("t1");
        teacher.setRoles(Set.of(Role.TEACHER));
        when(authentication.getName()).thenReturn("teacher@example.com");
        when(userService.getUserByEmail("teacher@example.com")).thenReturn(Optional.of(teacher));
        when(userService.hasRole(teacher, "TEACHER")).thenReturn(true);

        StudentProfile profile = new StudentProfile();
        User student = new User();
        student.setId("stu123");
        profile.setLinkedUser(student);

        when(rosterService.isStudentUnderTeacher("t1", "stu123")).thenReturn(false);

        var request = new ProfileController.UpdateStudentProfileRequest(1L, profile);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.updateStudentProfile(request, authentication));
        assertEquals("Access denied: Student is not under your roster.", ex.getMessage());
    }

    @Test
    void createNonStudentProfile_shouldDenyIfUserNotOwner() {
        User user = new User();
        user.setId("u1");
        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasAnyRole(eq(user), any())).thenReturn(false); // not admin
        when(userService.hasRole(user, "STUDENT")).thenReturn(false); // not student

        var request = new ProfileController.CreateNonStudentProfileRequest("anotherId", new NonStudentProfile());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.createNonStudentProfile(authentication, request));
        assertEquals("You can only create your own non-student profile.", ex.getMessage());
    }

    @Test
    void createNonStudentProfile_shouldDenyIfUserIsStudent() {
        User user = new User();
        user.setId("u1");
        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasAnyRole(eq(user), any())).thenReturn(false); // not admin
        when(userService.hasRole(user, "STUDENT")).thenReturn(true); // is student

        var request = new ProfileController.CreateNonStudentProfileRequest("u1", new NonStudentProfile());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.createNonStudentProfile(authentication, request));
        assertEquals("Students are not allowed to create non-student profiles.", ex.getMessage());
    }

    @Test
    void updateNonStudentProfile_shouldDenyIfUserNotOwnerOrAdmin() {
        User user = new User();
        user.setId("u1");
        when(authentication.getName()).thenReturn("user@example.com");
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(userService.hasAnyRole(eq(user), any())).thenReturn(false); // not admin

        NonStudentProfile profile = new NonStudentProfile();
        User another = new User();
        another.setId("other");
        profile.setLinkedUser(another);

        var request = new ProfileController.UpdateNonStudentProfileRequest(1L, profile);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                controller.updateNonStudentProfile(request, authentication));
        assertEquals("Access denied: You can only update your own non-student profile.", ex.getMessage());
    }


}
