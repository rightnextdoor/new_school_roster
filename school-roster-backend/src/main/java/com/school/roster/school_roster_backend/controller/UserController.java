package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // === Get All Users ===
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // === Get User by ID ===
    @PostMapping("/get")
    public ResponseEntity<User> getUserById(@RequestBody IdRequest request) {
        return userService.getUserById(request.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // === Update User ===
    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(request.getId(), request.getUpdatedUser());
        return ResponseEntity.ok(user);
    }

    // === Delete User ===
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody IdRequest request) {
        userService.deleteUser(request.getId());
        return ResponseEntity.ok("User deleted successfully.");
    }

    // === Request DTOs ===
    @Data
    @AllArgsConstructor
    private static class IdRequest {
        private String id;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateUserRequest {
        private String id;
        private User updatedUser;
    }
}
