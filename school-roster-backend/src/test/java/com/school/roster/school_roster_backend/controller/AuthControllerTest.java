package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.AuthenticationService;
import com.school.roster.school_roster_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // === Local Fake Login Request ===
    record FakeLoginRequest(String email, String password) {}

    @Test
    void signup_shouldCreateNewUser() {
        User user = new User();
        when(userService.createUser(any(User.class))).thenReturn(user);

        ResponseEntity<User> response = authController.signup(user);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void login_shouldReturnJwtToken() {
        FakeLoginRequest loginRequest = new FakeLoginRequest("test@example.com", "password123");

        when(authenticationService.authenticate(loginRequest.email(), loginRequest.password()))
                .thenReturn("fake-jwt-token");

        ResponseEntity<String> response = authController.login(
                new AuthController.LoginRequest(loginRequest.email(), loginRequest.password())
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("fake-jwt-token", response.getBody());
        verify(authenticationService, times(1)).authenticate(loginRequest.email(), loginRequest.password());
    }

    @Test
    void whoAmI_shouldReturnUserInfo() {
        String email = "test@example.com";
        User user = new User();
        user.setId("123456789012");
        user.setEmail(email);
        user.setRoles(Set.of());

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(java.util.Optional.of(user));

        ResponseEntity<AuthController.UserInfoResponse> response = authController.whoAmI(authentication);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user.getId(), response.getBody().id());
        assertEquals(user.getEmail(), response.getBody().email());
    }

    @Test
    void whoAmI_shouldThrowIfUserNotFound() {
        String email = "notfound@example.com";

        when(authentication.getName()).thenReturn(email);
        when(userService.getUserByEmail(email)).thenReturn(java.util.Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authController.whoAmI(authentication);
        });

        assertEquals("User not found with email: " + email, exception.getMessage());
    }
}
