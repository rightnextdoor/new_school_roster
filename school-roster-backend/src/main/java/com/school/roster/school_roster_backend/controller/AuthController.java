package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.AuthenticationService;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    // === Signup ===
    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    // === Login (UPDATED to use RequestBody) ===
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String token = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );
        return ResponseEntity.ok(token);
    }

    // === Who Am I ===
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> whoAmI(Authentication authentication) {
        String email = authentication.getName(); // from JWT token

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        UserInfoResponse response = new UserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getRoles()
        );

        return ResponseEntity.ok(response);
    }

    // === DTOs ===
    @Data
    @AllArgsConstructor
    private static class LoginRequest {
        private String email;
        private String password;
    }

    public record UserInfoResponse(String id, String email, Object roles) {}
}
