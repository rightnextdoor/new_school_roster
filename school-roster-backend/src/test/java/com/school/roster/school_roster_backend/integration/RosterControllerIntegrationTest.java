package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.RosterController;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.Role;
import com.school.roster.school_roster_backend.repository.RosterRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RosterControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RosterRepository rosterRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    public record LoginRequest(String email, String password) {}

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        rosterRepository.deleteAll();

        User teacher = new User();
        teacher.setId("teacher123");
        teacher.setEmail("teacheruser@example.com");
        teacher.setPassword(passwordEncoder.encode("password123"));
        teacher.setRoles(Set.of(Role.TEACHER)); // ✅ Must be TEACHER

        userRepository.save(teacher);

        LoginRequest loginRequest = new LoginRequest("teacheruser@example.com", "password123");

        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void createRoster_shouldReturnSuccess() throws Exception {
        Roster roster = new Roster();
        roster.setSubjectName("Math");
        roster.setPeriod("First Quarter");
        roster.setNickname("MATH101");
        roster.setGradeLevel("1");

        mockMvc.perform(post("/api/rosters/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roster)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subjectName").value("Math"));
    }

    @Test
    void getAllRosters_shouldReturnAccessDeniedForTeacher() throws Exception {
        mockMvc.perform(get("/api/rosters/getAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getRostersByStudent_shouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/api/rosters/getByStudent")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void getRostersByTeacher_shouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/rosters/getByTeacher")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void updateRoster_shouldReturnAccessDenied() throws Exception {
        Roster roster = new Roster();
        roster.setSubjectName("Science");
        roster.setPeriod("Second Quarter");
        roster.setNickname("SCI101");

        RosterController.UpdateRosterRequest updateRequest = new RosterController.UpdateRosterRequest(1L, roster);

        mockMvc.perform(put("/api/rosters/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteRoster_shouldReturnAccessDenied() throws Exception {
        RosterController.IdRequest deleteRequest = new RosterController.IdRequest(1L);

        mockMvc.perform(delete("/api/rosters/delete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void getRosterById_shouldReturnAccessDenied() throws Exception {
        RosterController.IdRequest getByIdRequest = new RosterController.IdRequest(1L);

        mockMvc.perform(post("/api/rosters/getById")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(getByIdRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void addStudentToRoster_shouldReturnAccessDenied() throws Exception {
        List<String> userId = new ArrayList<>();
        userId.add("student123");
        RosterController.AddStudentRequest addStudentRequest = new RosterController.AddStudentRequest(1L, userId);

        mockMvc.perform(post("/api/rosters/addStudent")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(addStudentRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void removeStudentFromRoster_shouldReturnAccessDenied() throws Exception {
        List<String> userId = new ArrayList<>();
        userId.add("student123");
        RosterController.AddStudentRequest removeStudentRequest = new RosterController.AddStudentRequest(1L, userId);

        mockMvc.perform(post("/api/rosters/removeStudent")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeStudentRequest)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void reassignTeacher_shouldReturnAccessDenied() throws Exception {
        RosterController.ReassignTeacherRequest reassignRequest = new RosterController.ReassignTeacherRequest(1L, "teacher456");

        mockMvc.perform(post("/api/rosters/reassignTeacher")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reassignRequest)))
                .andExpect(status().is4xxClientError());
    }
}
