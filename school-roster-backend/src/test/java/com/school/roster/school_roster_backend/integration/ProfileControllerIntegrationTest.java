package com.school.roster.school_roster_backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.roster.school_roster_backend.controller.ProfileController;
import com.school.roster.school_roster_backend.entity.NonStudentProfile;
import com.school.roster.school_roster_backend.entity.StudentProfile;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.NonStudentProfileRepository;
import com.school.roster.school_roster_backend.repository.StudentProfileRepository;
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
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private NonStudentProfileRepository nonStudentProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;

    public record LoginRequest(String email, String password) {}

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        studentProfileRepository.deleteAll();
        nonStudentProfileRepository.deleteAll();

        User user = new User();
        user.setId("student123");
        user.setEmail("profileuser@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user);

        // Insert linked StudentProfile
        StudentProfile profile = new StudentProfile();
        profile.setFirstName("Test");
        profile.setLastName("User");
        profile.setLinkedUser(user);
        studentProfileRepository.save(profile);

        LoginRequest loginRequest = new LoginRequest("profileuser@example.com", "password123");

        token = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getMyProfile_shouldReturnSuccess() throws Exception {
        mockMvc.perform(get("/api/profiles/getMyProfile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void getStudentProfileById_shouldReturnBadRequest() throws Exception {
        ProfileController.IdRequest request = new ProfileController.IdRequest(999L);

        mockMvc.perform(post("/api/profiles/student/getById")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getNonStudentProfileById_shouldReturnBadRequest() throws Exception {
        ProfileController.IdRequest request = new ProfileController.IdRequest(999L);

        mockMvc.perform(post("/api/profiles/nonstudent/getById")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProfileByUser_shouldReturnProfile() throws Exception {
        ProfileController.UserIdRequest request = new ProfileController.UserIdRequest("student123");

        mockMvc.perform(post("/api/profiles/getByUser")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getAllProfiles_shouldReturnAccessDeniedForStudent() throws Exception {
        mockMvc.perform(get("/api/profiles/getAll")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void createNonStudentProfile_shouldReturnSuccess() throws Exception {
        NonStudentProfile newProfile = new NonStudentProfile();
        newProfile.setFirstName("Carlos");
        newProfile.setLastName("Santos");

        ProfileController.CreateNonStudentProfileRequest request = new ProfileController.CreateNonStudentProfileRequest(
                "student123",
                newProfile
        );

        mockMvc.perform(post("/api/profiles/nonstudent/create")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updateNonStudentProfile_shouldReturnAccessDenied() throws Exception {
        NonStudentProfile profile = new NonStudentProfile();
        profile.setFirstName("UpdateFirst");
        profile.setLastName("UpdateLast");

        ProfileController.UpdateNonStudentProfileRequest request = new ProfileController.UpdateNonStudentProfileRequest(
                999L,
                profile
        );

        mockMvc.perform(put("/api/profiles/nonstudent/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteNonStudentProfile_shouldReturnAccessDenied() throws Exception {
        ProfileController.IdRequest request = new ProfileController.IdRequest(999L);

        mockMvc.perform(delete("/api/profiles/nonstudent/delete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void deleteStudentProfile_shouldReturnAccessDenied() throws Exception {
        ProfileController.IdRequest request = new ProfileController.IdRequest(999L);

        mockMvc.perform(delete("/api/profiles/student/delete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError());
    }
}
