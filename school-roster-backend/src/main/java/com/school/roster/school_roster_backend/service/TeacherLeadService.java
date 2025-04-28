package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeacherLeadService {

    private final UserRepository userRepository;

    public void addTeacher(String leadId, String teacherId, User caller) {
        User lead = userRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        // Check permission
        if (!canManageLead(lead, caller)) {
            throw new RuntimeException("Access denied: cannot manage this teacher lead.");
        }

        lead.getAssignedTeachers().add(teacher);
        userRepository.save(lead);
    }

    public void removeTeacher(String leadId, String teacherId, User caller) {
        User lead = userRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));

        if (!canManageLead(lead, caller)) {
            throw new RuntimeException("Access denied: cannot manage this teacher lead.");
        }

        lead.getAssignedTeachers().removeIf(teacher -> teacher.getId().equals(teacherId));
        userRepository.save(lead);
    }

    public List<User> getMyTeachers(User caller) {
        User lead = userRepository.findById(caller.getId())
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return lead.getAssignedTeachers();
    }

    public List<User> getTeachersForAdmin(String leadId) {
        User lead = userRepository.findById(leadId)
                .orElseThrow(() -> new RuntimeException("Lead not found"));
        return lead.getAssignedTeachers();
    }

    private boolean canManageLead(User lead, User caller) {
        return lead.getId().equals(caller.getId()) ||
                caller.getRoles().stream().anyMatch(role ->
                        role.name().equals("ADMIN") || role.name().equals("ADMINISTRATOR")
                );
    }
}
