package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController controller;

    @Test
    void getAllUsers_shouldReturnListOfUsers() {
        List<User> users = List.of(new User(), new User());
        when(userService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = controller.getAllUsers();

        assertEquals(2, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_shouldReturnUserIfExists() {
        User user = new User();
        when(userService.getUserById("userId")).thenReturn(Optional.of(user));

        UserController.IdRequest request = new UserController.IdRequest("userId");
        ResponseEntity<User> response = controller.getUserById(request);

        assertEquals(user, response.getBody());
        verify(userService).getUserById("userId");
    }

    @Test
    void getUserById_shouldReturn404IfNotFound() {
        when(userService.getUserById("missingId")).thenReturn(Optional.empty());

        UserController.IdRequest request = new UserController.IdRequest("missingId");
        ResponseEntity<User> response = controller.getUserById(request);

        assertEquals(404, response.getStatusCodeValue());
        verify(userService).getUserById("missingId");
    }

    @Test
    void updateUser_shouldUpdateAndReturnUser() {
        User updatedUser = new User();
        when(userService.updateUser("userId", updatedUser)).thenReturn(updatedUser);

        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest("userId", updatedUser);
        ResponseEntity<User> response = controller.updateUser(request);

        assertEquals(updatedUser, response.getBody());
        verify(userService).updateUser("userId", updatedUser);
    }

    @Test
    void deleteUser_shouldDeleteAndReturnSuccessMessage() {
        UserController.IdRequest request = new UserController.IdRequest("userId");

        ResponseEntity<String> response = controller.deleteUser(request);

        verify(userService).deleteUser("userId");
        assertEquals("User deleted successfully.", response.getBody());
    }

    @Test
    void getMyUser_shouldReturnCurrentUser() {
        when(authentication.getName()).thenReturn("user@example.com");

        User user = new User();
        when(userService.getUserByEmail("user@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = controller.getMyUser(authentication);

        assertEquals(user, response.getBody());
        verify(userService).getUserByEmail("user@example.com");
    }
}
