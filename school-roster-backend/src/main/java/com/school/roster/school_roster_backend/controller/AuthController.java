package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.service.AuthenticationService;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    // === Signup ===
    @PostMapping("/signup")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMINISTRATOR', 'ADMIN')")
    public ResponseEntity<User> signup(@RequestBody User user) {
        // Prevent users from assigning themselves Admin-level roles
        if (user.getRoles() != null) {
            boolean tryingToAddAdmin = user.getRoles().stream().anyMatch(role ->
                    role.name().equalsIgnoreCase("ADMIN")
            );
            if (tryingToAddAdmin) {
                throw new RuntimeException("You are not allowed to assign admin roles during signup.");
            }
        }

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
    @PreAuthorize("isAuthenticated()")
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

    @PostMapping("/role")
    @PreAuthorize("isAuthenticated()")
    public  ResponseEntity<UserInfoResponse> updateRole(@RequestBody RoleUpdate roleUpdate){
        UserInfoResponse response = userService.updateRole(roleUpdate);
        return ResponseEntity.ok(response);
    }

    // === DTOs ===
    @Data
    @AllArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Data
    @AllArgsConstructor
    public static class RoleUpdate{
        private String userId;
        private Set<Role> roles;
    }

    public record UserInfoResponse(String id, String email, Object roles) {}
}
