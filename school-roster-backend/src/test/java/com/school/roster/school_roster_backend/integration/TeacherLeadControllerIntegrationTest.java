package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.TeacherLeadController;
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
class TeacherLeadControllerIntegrationTest {

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

        User lead = new User();
        lead.setId("lead123");
        lead.setEmail("leaduser@example.com");
        lead.setPassword(passwordEncoder.encode("password123"));
        lead.setRoles(Set.of(Role.TEACHER_LEAD)); // ✅ Must be TEACHER_LEAD

        userRepository.save(lead);

        LoginRequest loginRequest = new LoginRequest("leaduser@example.com", "password123");

        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void addTeacher_shouldReturnOk() throws Exception {
        // Seed teacher lead
        User lead = new User();
        lead.setId("lead123");
        lead.setEmail("lead@example.com");
        lead.setPassword(passwordEncoder.encode("password123"));
        lead.setRoles(Set.of(Role.TEACHER_LEAD));
        userRepository.save(lead);

        // Seed teacher
        User teacher = new User();
        teacher.setId("teacher456");
        teacher.setEmail("teacher@example.com");
        teacher.setPassword(passwordEncoder.encode("password123"));
        teacher.setRoles(Set.of(Role.TEACHER));
        userRepository.save(teacher);

        // Login as the lead
        RosterControllerIntegrationTest.LoginRequest loginRequest = new RosterControllerIntegrationTest.LoginRequest("lead@example.com", "password123");
        String leadToken = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Perform the request
        TeacherLeadController.ManageTeacherRequest request = new TeacherLeadController.ManageTeacherRequest("lead123", "teacher456");

        mockMvc.perform(post("/api/leads/addTeacher")
                        .header("Authorization", "Bearer " + leadToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Teacher added to lead."));
    }


    @Test
    void removeTeacher_shouldReturnOk() throws Exception {
        TeacherLeadController.ManageTeacherRequest request = new TeacherLeadController.ManageTeacherRequest("lead123", "teacher456");

        mockMvc.perform(post("/api/leads/removeTeacher")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Teacher removed from lead."));
    }

    @Test
    void getMyTeachers_shouldReturnEmptyInitially() throws Exception {
        mockMvc.perform(get("/api/leads/getMyTeachers")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTeachersByLeadId_shouldReturnForbiddenForTeacherLead() throws Exception {
        TeacherLeadController.LeadIdRequest request = new TeacherLeadController.LeadIdRequest("lead123");

        mockMvc.perform(post("/api/leads/getTeachersByLeadId")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
