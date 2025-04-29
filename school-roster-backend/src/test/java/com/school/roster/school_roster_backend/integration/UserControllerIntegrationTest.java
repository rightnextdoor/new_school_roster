package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.UserController;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    public record LoginRequest(String email, String password) {}

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        User admin = new User();
        admin.setId("admin123");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("adminpass"));
        admin.setRoles(Set.of(Role.ADMINISTRATOR));
        userRepository.save(admin);

        LoginRequest loginRequest = new LoginRequest("admin@example.com", "adminpass");

        adminToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllUsers_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        User user = new User();
        user.setId("u100");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("pass"));
        userRepository.save(user);

        UserController.IdRequest request = new UserController.IdRequest("u100");

        mockMvc.perform(post("/api/users/get")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        User user = new User();
        user.setId("u101");
        user.setEmail("updateuser@example.com");
        user.setPassword(passwordEncoder.encode("oldpass"));
        userRepository.save(user);

        user.setEmail("updated@example.com");

        UserController.UpdateUserRequest request = new UserController.UpdateUserRequest("u101", user);

        mockMvc.perform(put("/api/users/update")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void deleteUser_shouldReturnSuccess() throws Exception {
        User user = new User();
        user.setId("u102");
        user.setEmail("deleteuser@example.com");
        user.setPassword(passwordEncoder.encode("delpass"));
        userRepository.save(user);

        UserController.IdRequest request = new UserController.IdRequest("u102");

        mockMvc.perform(delete("/api/users/delete")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully."));
    }

    @Test
    void getMyUser_shouldReturnCurrentUser() throws Exception {
        mockMvc.perform(post("/api/users/getMyUser")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@example.com"));
    }
}
