package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.service.RosterService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rosters")
@RequiredArgsConstructor
public class RosterController {

    private final RosterService rosterService;

    // === Create Roster ===
    @PostMapping("/create")
    public ResponseEntity<Roster> createRoster(@RequestBody CreateRosterRequest request) {
        Roster roster = rosterService.createRoster(request.getRoster(), request.getTeacherId());
        return ResponseEntity.ok(roster);
    }

    // === Update Roster ===
    @PutMapping("/update")
    public ResponseEntity<Roster> updateRoster(@RequestBody UpdateRosterRequest request) {
        Roster updated = rosterService.updateRoster(request.getRosterId(), request.getUpdatedRoster());
        return ResponseEntity.ok(updated);
    }

    // === Delete Roster ===
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteRoster(@RequestBody RosterIdRequest request) {
        rosterService.deleteRoster(request.getRosterId());
        return ResponseEntity.ok("Roster deleted successfully.");
    }

    // === Add Student to Roster ===
    @PostMapping("/addStudent")
    public ResponseEntity<Roster> addStudentToRoster(@RequestBody RosterStudentRequest request) {
        Roster updatedRoster = rosterService.addStudentToRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(updatedRoster);
    }

    // === Remove Student from Roster ===
    @PostMapping("/removeStudent")
    public ResponseEntity<Roster> removeStudentFromRoster(@RequestBody RosterStudentRequest request) {
        Roster updatedRoster = rosterService.removeStudentFromRoster(request.getRosterId(), request.getStudentId());
        return ResponseEntity.ok(updatedRoster);
    }

    // === Reassign Teacher for a Roster ===
    @PostMapping("/reassignTeacher")
    public ResponseEntity<Roster> reassignTeacher(@RequestBody ReassignTeacherRequest request) {
        Roster updatedRoster = rosterService.reassignTeacher(request.getRosterId(), request.getNewTeacherId());
        return ResponseEntity.ok(updatedRoster);
    }

    // === GET Roster Methods ===

    // --- Get Roster by Roster ID ---
    @PostMapping("/getById")
    public ResponseEntity<Roster> getRosterById(@RequestBody RosterIdRequest request) {
        Roster roster = rosterService.getRosterById(request.getRosterId());
        return ResponseEntity.ok(roster);
    }

    // --- Get Rosters by Student ID ---
    @PostMapping("/getByStudent")
    public ResponseEntity<List<Roster>> getRostersByStudent(@RequestBody StudentIdRequest request) {
        List<Roster> rosters = rosterService.getRostersByStudentId(request.getStudentId());
        return ResponseEntity.ok(rosters);
    }

    // --- Get Rosters by Teacher ID ---
    @PostMapping("/getByTeacher")
    public ResponseEntity<List<Roster>> getRostersByTeacher(@RequestBody TeacherIdRequest request) {
        List<Roster> rosters = rosterService.getRostersByTeacherId(request.getTeacherId());
        return ResponseEntity.ok(rosters);
    }

    // --- Get All Rosters ---
    @GetMapping("/getAll")
    public ResponseEntity<List<Roster>> getAllRosters() {
        List<Roster> rosters = rosterService.getAllRosters();
        return ResponseEntity.ok(rosters);
    }

    // === Request DTOs ===

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
}
