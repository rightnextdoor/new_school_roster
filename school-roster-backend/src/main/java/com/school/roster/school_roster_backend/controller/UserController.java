package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;


import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === Get All Users ===
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // === Get User by ID ===
    @PostMapping("/get")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getUserById(@RequestBody IdRequest request) {
        return userService.getUserById(request.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === Update User ===
    @PutMapping("/update")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(request.getId(), request.getUpdatedUser());
        return ResponseEntity.ok(user);
    }

    // === Delete User ===
    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR')")
    public ResponseEntity<String> deleteUser(@RequestBody IdRequest request) {
        userService.deleteUser(request.getId());
        return ResponseEntity.ok("User deleted successfully.");
    }

    // === Get Logged-in User Info ===
    @PostMapping("/getMyUser")
    public ResponseEntity<User> getMyUser(Authentication authentication) {
        String email = authentication.getName(); // From the JWT token

        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        return ResponseEntity.ok(user);
    }


    // === Request DTOs ===
    @Data
    @AllArgsConstructor
    public static class IdRequest {
        private String id;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String id;
        private User updatedUser;
    }
}
