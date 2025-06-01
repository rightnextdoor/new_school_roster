package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.repository.GradeRepository;
import com.school.roster.school_roster_backend.repository.RosterRepository;
import com.school.roster.school_roster_backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RosterService {

    private final RosterRepository rosterRepository;
    private final UserRepository userRepository;
    private final GradeRepository gradeRepository;
    private final GradeService gradeService;
    private final HighestPossibleScoreService highestPossibleScoreService;

    public RosterService(
            RosterRepository rosterRepository,
            UserRepository userRepository,
            GradeRepository gradeRepository,
            @Lazy GradeService gradeService,
            HighestPossibleScoreService highestPossibleScoreService
    ) {
        this.rosterRepository = rosterRepository;
        this.userRepository = userRepository;
        this.gradeRepository = gradeRepository;
        this.gradeService = gradeService;
        this.highestPossibleScoreService = highestPossibleScoreService;
    }

    // === Create Roster ===
    public Roster createRoster(Roster incomingRoster, String teacherId) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));

        Roster roster = new Roster();
        roster.setSubjectName(incomingRoster.getSubjectName());
        roster.setPeriod(incomingRoster.getPeriod());
        roster.setNickname(incomingRoster.getNickname());
        roster.setTeacher(teacher);
        roster.setStudents(new ArrayList<>());
        roster.setGrades(new ArrayList<>());
        roster.setClassGpa(0f);

        Roster savedRoster = rosterRepository.save(roster);

        HighestPossibleScore hps = highestPossibleScoreService.createHighestPossibleScore(savedRoster);
        savedRoster.setHighestPossibleScore(hps);

        return rosterRepository.save(savedRoster);
    }

    public void recalculateClassGpa(Long rosterId) {
        // 1) Fetch the roster (including its grades collection)
        Roster roster = rosterRepository.findById(rosterId)
                .orElseThrow(() -> new RuntimeException("Roster not found with ID: " + rosterId));

        var grades = roster.getGrades();
        if (grades == null || grades.isEmpty()) {
            roster.setClassGpa(0.0f);
        } else {
            double sum = 0.0;
            int count = 0;
            for (var grade : grades) {
                Double initial = grade.getInitialGrade();
                if (initial != null) {
                    sum += initial;
                }
                count++;
            }
            double avg = (count > 0) ? (sum / count) : 0.0;
            // Round to two decimals, then cast to float
            float roundedAvg = (float) (Math.round(avg * 100.0) / 100.0);
            roster.setClassGpa(roundedAvg);
        }

        // 2) Save the roster so the new GPA is persisted
        rosterRepository.save(roster);
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
            Grade grade = gradeService.createGrade(roster, student);
            roster.getGrades().add(grade);
        }

        return rosterRepository.save(roster);
    }

    public boolean isStudentUnderTeacher(String teacherId, String studentId) {
        List<Roster> rosters = rosterRepository.findByTeacherId(teacherId);
        for (Roster roster : rosters) {
            for (User student : roster.getStudents()) {
                if (student.getId().equals(studentId)) {
                    return true;
                }
            }
        }
        return false;
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
        return rosterRepository.findByIdWithTeacherAndStudents(id)
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

    public boolean canEditRoster(Long rosterId, User currentUser) {
        Roster roster = getRosterById(rosterId);

        // ✅ If the current user is the teacher assigned to the roster
        if (roster.getTeacher() != null && roster.getTeacher().getId().equals(currentUser.getId())) {
            return true;
        }

        // ✅ Or if user is admin, administrator, teacher_lead
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.name().equals("ADMIN") ||
                        role.name().equals("ADMINISTRATOR") ||
                        role.name().equals("TEACHER_LEAD"));
    }

    public boolean canViewRoster(Long rosterId, User currentUser) {
        Roster roster = getRosterById(rosterId);

        // ✅ If the current user is the teacher assigned
        if (roster.getTeacher() != null && roster.getTeacher().getId().equals(currentUser.getId())) {
            return true;
        }

        // ✅ If the current user is a student in this roster
        if (roster.getStudents().stream().anyMatch(student -> student.getId().equals(currentUser.getId()))) {
            return true;
        }

        // ✅ Or if user is admin, administrator, office administrator
        return currentUser.getRoles().stream()
                .anyMatch(role -> role.name().equals("ADMIN") ||
                        role.name().equals("ADMINISTRATOR") ||
                        role.name().equals("OFFICE_ADMINISTRATOR"));
    }

}
