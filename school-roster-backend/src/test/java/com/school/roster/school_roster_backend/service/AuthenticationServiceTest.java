package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.UserRepository;
import com.school.roster.school_roster_backend.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;


import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticate_validCredentials_shouldReturnToken() {
        // Arrange
        String email = "test@example.com";
        String password = "password";
        String token = "mockToken";

        User user = User.builder()
                .email(email)
                .password(password)
                .roles(Set.of())
                .build();

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtUtils.generateToken(eq(email), anyList()))
                .thenReturn(token);

        // Act
        String result = authenticationService.authenticate(email, password);

        // Assert
        assertEquals(token, result);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail(email);
        verify(jwtUtils, times(1)).generateToken(eq(email), anyList());
    }

    @Test
    void authenticate_invalidCredentials_shouldThrowException() {
        // Arrange
        String email = "test@example.com";
        String password = "wrongPassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate(email, password);
        });

        assertEquals("Invalid email or password.", exception.getMessage());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(any());
        verify(jwtUtils, never()).generateToken(any(), anyList());
    }

}
