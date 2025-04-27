package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RosterService {

    private final RosterRepository rosterRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;

    // === Create Roster ===
    public Roster createRoster(Roster roster, String teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));

        roster.setTeacher(teacher);
        roster.setStudents(new ArrayList<>());
        roster.setGrades(new ArrayList<>());
        roster.setClassGpa(0f);

        return rosterRepository.save(roster);
    }

    // === Assign Student to Roster ===
    public Roster addStudentToRoster(Long rosterId, String studentId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        if (!roster.getStudents().contains(student)) {
            roster.getStudents().add(student);

            // Create empty Grade object when student is added
            Grade grade = Grade.builder()
                    .student(student)
                    .roster(roster)
                    .performanceScores(new ArrayList<>())
                    .quizScores(new ArrayList<>())
                    .quarterlyExamScores(new ArrayList<>())
                    .finalGpa(0f)
                    .build();

            gradeRepository.save(grade);
            roster.getGrades().add(grade);
        }

        return rosterRepository.save(roster);
    }

    // === Remove Student from Roster ===
    public Roster removeStudentFromRoster(Long rosterId, String studentId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

        roster.getStudents().remove(student);

        // Delete grade linked to this roster + student
        List<Grade> grades = gradeRepository.findByRosterId(roster.getId());
        grades.stream()
                .filter(g -> g.getStudent().getId().equals(studentId))
                .forEach(gradeRepository::delete);

        return rosterRepository.save(roster);
    }

    // === Update Roster Details ===
    public Roster updateRoster(Long rosterId, Roster updatedData) {
        Roster existingRoster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        existingRoster.setSubjectName(updatedData.getSubjectName());
        existingRoster.setPeriod(updatedData.getPeriod());
        existingRoster.setNickname(updatedData.getNickname());

        return rosterRepository.save(existingRoster);
    }

    // === Reassign Teacher to Roster ===
    public Roster reassignTeacher(Long rosterId, String newTeacherId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        User newTeacher = userRepository.findById(newTeacherId)
                .orElseThrow(() -> new RuntimeException("New teacher not found with ID: " + newTeacherId));

        roster.setTeacher(newTeacher);

        return rosterRepository.save(roster);
    }

    // === Delete Roster (and all Grades linked to it) ===
    public void deleteRoster(Long rosterId) {
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        // Delete all Grades linked to this Roster
        List<Grade> grades = gradeRepository.findByRosterId(rosterId);
        gradeRepository.deleteAll(grades);

        // Unlink Students (optional: to clear join table if needed)
        roster.getStudents().clear();

        // Then delete Roster
        rosterRepository.delete(roster);
    }

    // === Find Roster by ID ===
    public Roster getRosterById(Long id) {
        return rosterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + id));
    }

    // === Find Rosters by Student ID ===
    public List<Roster> getRostersByStudentId(String studentId) {
        List<Roster> result = new ArrayList<>();
        List<Roster> allRosters = rosterRepository.findAll();
        for (Roster roster : allRosters) {
            for (User student : roster.getStudents()) {
                if (student.getId().equals(studentId)) {
                    result.add(roster);
                    break;
                }
            }
        }
        return result;
    }

    // === Find Rosters by Teacher ID ===
    public List<Roster> getRostersByTeacherId(String teacherId) {
        return rosterRepository.findByTeacherId(teacherId);
    }

    // === Get All Rosters ===
    public List<Roster> getAllRosters() {
        return rosterRepository.findAll();
    }

}
