package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.GradeController;
import com.school.roster.school_roster_backend.entity.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GradeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    public record LoginRequest(String email, String password) {}

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        User user = new User();
        user.setId("student123");
        user.setEmail("gradeuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest("gradeuser@example.com", "password123");

        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getMyGpa_shouldReturnEmptyListInitially() throws Exception {
        mockMvc.perform(get("/api/grades/myGpa")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentGpa").value(0.0))
                .andExpect(jsonPath("$.subjects").isArray());
    }

    @Test
    void updateGrade_shouldReturnAccessDenied() throws Exception {
        // Now using Integer lists for scores
        GradeController.UpdateGradeRequest updateRequest = new GradeController.UpdateGradeRequest(
                1L,
                1L,
                java.util.List.of(90),
                java.util.List.of(80),
                java.util.List.of(85)
        );

        mockMvc.perform(put("/api/grades/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteGrade_shouldReturnAccessDenied() throws Exception {
        GradeController.GradeIdRequest deleteRequest = new GradeController.GradeIdRequest(1L);

        mockMvc.perform(delete("/api/grades/delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getGradeById_shouldReturnAccessDenied() throws Exception {
        GradeController.GradeIdRequest getByIdRequest = new GradeController.GradeIdRequest(1L);

        mockMvc.perform(post("/api/grades/getById")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(getByIdRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getGradesByRoster_shouldReturnAccessDenied() throws Exception {
        GradeController.RosterIdRequest getByRosterRequest = new GradeController.RosterIdRequest(1L);

        mockMvc.perform(post("/api/grades/getByRoster")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(getByRosterRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getGradesByStudent_shouldReturnAccessDenied() throws Exception {
        // Authenticated user is student123, we request grades for someone else
        GradeController.StudentIdRequest getByStudentRequest = new GradeController.StudentIdRequest("someone_else");

        mockMvc.perform(post("/api/grades/getByStudent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(getByStudentRequest)))
                .andExpect(status().is4xxClientError()); // should now be 403 or 4xx
    }

}
