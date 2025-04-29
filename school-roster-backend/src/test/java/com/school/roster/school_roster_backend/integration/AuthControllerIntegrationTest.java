package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.AuthController.LoginRequest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For JSON conversion

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean DB before each test
    }

    @Test
    void signup_shouldCreateUserSuccessfully() throws Exception {
        // Setup admin
        User adminUser = new User();
        adminUser.setId("admin123");
        adminUser.setEmail("admin@example.com");
        adminUser.setPassword(passwordEncoder.encode("adminpassword"));
        adminUser.setRoles(Set.of(Role.ADMIN));
        userRepository.save(adminUser);

        // Login admin
        LoginRequest loginRequest = new LoginRequest("admin@example.com", "adminpassword");

        String adminToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Now create normal user
        User user = new User();
        user.setId("user1");
        user.setEmail("testuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + adminToken)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testuser@example.com"));

        // Verify saved
        assertThat(userRepository.findByEmail("testuser@example.com")).isPresent();
    }


    @Test
    void login_shouldReturnJwtToken() throws Exception {
        // Create a user manually in DB
        User user = new User();
        user.setId("user2"); // Add this line
        user.setEmail("loginuser@example.com");
        user.setPassword(passwordEncoder.encode("mypassword"));

        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("loginuser@example.com", "mypassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.startsWith("ey"))); // JWT usually starts with "ey"
    }

    @Test
    void whoAmI_shouldReturnUserInfo() throws Exception {
        // Create and login a user to get a token
        User user = new User();
        user.setId("student123"); // 🔥 ADD THIS
        user.setEmail("whoami@example.com");
        user.setPassword(passwordEncoder.encode("secret"));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("whoami@example.com", "secret");

        String token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("whoami@example.com"));
    }

}
