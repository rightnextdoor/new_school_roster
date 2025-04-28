package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.Grade;
import com.school.roster.school_roster_backend.service.RosterService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rosters")
@RequiredArgsConstructor
public class RosterController {

    private final RosterService rosterService;

    @PostMapping("/create")
    public ResponseEntity<RosterResponse> createRoster(@RequestBody CreateRosterRequest request) {
        Roster roster = rosterService.createRoster(request.getRoster(), request.getTeacherId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @PutMapping("/update")
    public ResponseEntity<RosterResponse> updateRoster(@RequestBody UpdateRosterRequest request) {
        Roster roster = rosterService.updateRoster(request.getRosterId(), request.getUpdatedRoster());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoster(@RequestBody RosterIdRequest request) {
        rosterService.deleteRoster(request.getRosterId());
        return ResponseEntity.ok("Roster deleted successfully.");
    }

    @PostMapping("/addStudent")
    public ResponseEntity<RosterResponse> addStudentToRoster(@RequestBody RosterStudentRequest request) {
        Roster roster = rosterService.addStudentToRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @PostMapping("/removeStudent")
    public ResponseEntity<RosterResponse> removeStudentFromRoster(@RequestBody RosterStudentRequest request) {
        Roster roster = rosterService.removeStudentFromRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @PostMapping("/reassignTeacher")
    public ResponseEntity<RosterResponse> reassignTeacher(@RequestBody ReassignTeacherRequest request) {
        Roster roster = rosterService.reassignTeacher(request.getRosterId(), request.getNewTeacherId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @PostMapping("/getById")
    public ResponseEntity<RosterResponse> getRosterById(@RequestBody RosterIdRequest request) {
        Roster roster = rosterService.getRosterById(request.getRosterId());
        return ResponseEntity.ok(buildRosterResponse(roster));
    }

    @PostMapping("/getByStudent")
    public ResponseEntity<List<RosterResponse>> getRostersByStudent(@RequestBody StudentIdRequest request) {
        List<Roster> rosters = rosterService.getRostersByStudentId(request.getStudentId());
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }

    @PostMapping("/getByTeacher")
    public ResponseEntity<List<RosterResponse>> getRostersByTeacher(@RequestBody TeacherIdRequest request) {
        List<Roster> rosters = rosterService.getRostersByTeacherId(request.getTeacherId());
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<RosterResponse>> getAllRosters() {
        List<Roster> rosters = rosterService.getAllRosters();
        return ResponseEntity.ok(rosters.stream().map(this::buildRosterResponse).collect(Collectors.toList()));
    }

    // === Build Response ===
    private RosterResponse buildRosterResponse(Roster roster) {
        return new RosterResponse(
                roster.getId(),
                roster.getSubjectName(),
                roster.getPeriod(),
                roster.getNickname(),
                roster.getTeacher() != null && roster.getTeacher().getNonStudentProfile() != null ? roster.getTeacher().getNonStudentProfile().getFirstName() : null,
                roster.getTeacher() != null && roster.getTeacher().getNonStudentProfile() != null ? roster.getTeacher().getNonStudentProfile().getLastName() : null,
                roster.getStudents().stream().map(student -> {
                    Grade grade = roster.getGrades().stream()
                            .filter(g -> g.getStudent() != null && g.getStudent().getId().equals(student.getId()))
                            .findFirst()
                            .orElse(null);
                    return new StudentInfo(
                            student.getId(),
                            student.getStudentProfile() != null ? student.getStudentProfile().getFirstName() : null,
                            student.getStudentProfile() != null ? student.getStudentProfile().getLastName() : null,
                            grade != null ? grade.getFinalGpa() : null
                    );
                }).collect(Collectors.toList()),
                roster.getClassGpa()
        );
    }

    // === DTOs ===

    @Data
    @AllArgsConstructor
    private static class CreateRosterRequest {
        private Roster roster;
        private String teacherId;
    }

    @Data
    @AllArgsConstructor
    private static class UpdateRosterRequest {
        private Long rosterId;
        private Roster updatedRoster;
    }

    @Data
    @AllArgsConstructor
    private static class RosterIdRequest {
        private Long rosterId;
    }

    @Data
    @AllArgsConstructor
    private static class RosterStudentRequest {
        private Long rosterId;
        private String studentId;
    }

    @Data
    @AllArgsConstructor
    private static class ReassignTeacherRequest {
        private Long rosterId;
        private String newTeacherId;
    }

    @Data
    @AllArgsConstructor
    private static class StudentIdRequest {
        private String studentId;
    }

    @Data
    @AllArgsConstructor
    private static class TeacherIdRequest {
        private String teacherId;
    }

    @Data
    @AllArgsConstructor
    private static class RosterResponse {
        private Long rosterId;
        private String subjectName;
        private String period;
        private String nickname;
        private String teacherFirstName;
        private String teacherLastName;
        private List<StudentInfo> students;
        private Float classGpa;
    }

    @Data
    @AllArgsConstructor
    private static class StudentInfo {
        private String studentId;
        private String firstName;
        private String lastName;
        private Float finalGpa;
    }
}
