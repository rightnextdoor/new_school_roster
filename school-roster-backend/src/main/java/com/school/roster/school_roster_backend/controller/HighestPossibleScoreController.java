package com.school.roster.school_roster_backend.controller;

import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import com.school.roster.school_roster_backend.entity.User;
import com.school.roster.school_roster_backend.entity.enums.CategoryType;
import com.school.roster.school_roster_backend.entity.enums.OperationType;
import com.school.roster.school_roster_backend.service.HighestPossibleScoreService;
import com.school.roster.school_roster_backend.service.RosterService;
import com.school.roster.school_roster_backend.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for all HighestPossibleScore (HPS) operations.
 * All endpoints accept exactly one @RequestBody DTO, defined as nested classes below.
 */
@RestController
@RequestMapping("/api/hps")
@RequiredArgsConstructor
public class HighestPossibleScoreController {

    private final HighestPossibleScoreService hpsService;
    private final RosterService rosterService;
    private final UserService userService;

    /**
     * View (fetch) the HPS for a given roster.
     * Request body: { "rosterId": 123 }
     * Response: the full HighestPossibleScore entity (including embedded ScoreDetails).
     */
    @PostMapping("/view")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<HighestPossibleScore> getHps(@RequestBody GetHpsRequest req, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (!rosterService.canEditRoster(req.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update this roster.");
        }

        HighestPossibleScore hps = hpsService.getByRosterId(req.getRosterId());
        return ResponseEntity.ok(hps);
    }

    /**
     * Add or remove a slot in one of the three categories (PERFORMANCE, QUIZ, EXAM).
     * Request body fields:
     *   - rosterId   (Long)
     *   - category   (String: "PERFORMANCE" | "QUIZ" | "EXAM")
     *   - operation  (String: "ADD" | "REMOVE")
     *   - index      (Integer, used only for REMOVE)
     *   - maxScore   (Integer, used only for ADD)
     */
    @PostMapping("/slots")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<HighestPossibleScore> changeSlot(@RequestBody SlotChangeRequest req, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (!rosterService.canEditRoster(req.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update this roster.");
        }

        HighestPossibleScore updated = hpsService.updateSlot(req);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update the PS (percentage‐scores) for each category (performance, quiz, exam).
     * Request body: { rosterId, performancePs, quizPs, quarterlyExamPs }
     */
    @PostMapping("/ps")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<HighestPossibleScore> updatePs(@RequestBody UpdatePsRequest req, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (!rosterService.canEditRoster(req.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update this roster.");
        }

        HighestPossibleScore updated = hpsService.updatePs(req);
        return ResponseEntity.ok(updated);
    }

    /**
     * Update the WS (weight‐scores) for each category (performance, quiz, exam).
     * Request body: { rosterId, performanceWs, quizWs, quarterlyExamWs }
     */
    @PostMapping("/ws")
    @PreAuthorize("hasAnyRole('TEACHER', 'TEACHER_LEAD')")
    public ResponseEntity<HighestPossibleScore> updateWs(@RequestBody UpdateWsRequest req, Authentication authentication) {
        String userEmail = authentication.getName();
        User currentUser = userService.getUserByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (!rosterService.canEditRoster(req.getRosterId(), currentUser)) {
            throw new RuntimeException("Access denied: You are not allowed to update this roster.");
        }

        HighestPossibleScore updated = hpsService.updateWs(req);
        return ResponseEntity.ok(updated);
    }

    // ────────────────────────────────────────────────────────────────────────────
    // Nested DTO classes (mimicking the pattern in other controllers)
    // ────────────────────────────────────────────────────────────────────────────

    @Data
    public static class GetHpsRequest {
        private Long rosterId;
    }

    @Data
    public static class SlotChangeRequest {
        private Long rosterId;
        private CategoryType category;   // "PERFORMANCE", "QUIZ", or "EXAM"
        private OperationType operation;  // "ADD" or "REMOVE"
        private Integer index;     // used only for REMOVE (0-based index)
        private Integer maxScore;  // used only for ADD (max value of new slot)
    }

    @Data
    public static class UpdatePsRequest {
        private Long rosterId;
        private Double performancePs;
        private Double quizPs;
        private Double quarterlyExamPs;
    }

    @Data
    public static class UpdateWsRequest {
        private Long rosterId;
        private Double performanceWs;
        private Double quizWs;
        private Double quarterlyExamWs;
    }
}
