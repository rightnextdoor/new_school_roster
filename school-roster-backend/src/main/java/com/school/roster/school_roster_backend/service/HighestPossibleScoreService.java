package com.school.roster.school_roster_backend.service;

import com.school.roster.school_roster_backend.controller.HighestPossibleScoreController;
import com.school.roster.school_roster_backend.entity.HighestPossibleScore;
import com.school.roster.school_roster_backend.entity.Roster;
import com.school.roster.school_roster_backend.entity.enums.CategoryType;
import com.school.roster.school_roster_backend.entity.enums.OperationType;
import com.school.roster.school_roster_backend.repository.HighestPossibleScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * Service to manage HighestPossibleScore operations.
 * Delegates all grade‐mutation logic to GradeService so that grades remain in one place.
 */
@Service
@Transactional
public class HighestPossibleScoreService {

    private final HighestPossibleScoreRepository hpsRepository;
    private final GradeService gradeService;
    public HighestPossibleScoreService(
            HighestPossibleScoreRepository hpsRepository,
            @Lazy GradeService gradeService
    ) {
        this.hpsRepository = hpsRepository;
        this.gradeService = gradeService;
    }

    /**
     * Fetch the HPS for a given rosterId (throws if not found).
     */
    @Transactional(readOnly = true)
    public HighestPossibleScore getByRosterId(Long rosterId) {
        return hpsRepository.findByRosterId(rosterId)
                .orElseThrow(() -> new RuntimeException("HPS not found for roster " + rosterId));
    }

    public HighestPossibleScore createHighestPossibleScore(Roster roster){
        HighestPossibleScore hps = new HighestPossibleScore();
        hps.setRoster(roster);

        var details = hps.getScoreDetails();
        details.getPerformanceScores().add(0);
        details.getQuizScores().add(0);
        details.getQuarterlyExamScores().add(0);

        // Totals are 0 by default
        details.setPerformancePs(100.0);
        details.setQuizPs(100.0);
        details.setQuarterlyExamPs(100.0);

        details.setPerformanceWs(40.0);
        details.setQuizWs(40.0);
        details.setQuarterlyExamWs(20.0);

        // Recalc totals (still 0)
        details.recalcTotals();

        // 3) Save HPS (cascade = ALL on Roster→HPS will handle this as well, but saving explicitly is fine)
        hpsRepository.save(hps);
        return hps;
    }

    /**
     * Add or remove a slot in the specified category. Does NOT hard-code PS values.
     * Delegates all grade updates to GradeService.propagateSlotChange(...).
     */

    public HighestPossibleScore updateSlot(HighestPossibleScoreController.SlotChangeRequest req) {
        Long rosterId = req.getRosterId();
        CategoryType category = req.getCategory();   // "PERFORMANCE", "QUIZ", or "EXAM"
        OperationType operation = req.getOperation();        // "ADD" or "REMOVE"
        Integer index = req.getIndex();        // used only for REMOVE
        Integer maxScore = req.getMaxScore();  // used only for ADD

        // 1) Fetch and mutate HPS’s score lists
        HighestPossibleScore hps = getByRosterId(rosterId);
        var hpsDetails = hps.getScoreDetails();

        if (operation == OperationType.REMOVE || operation == OperationType.UPDATE) {
            if (index == null || index < 0) {
                throw new IllegalArgumentException("Index must be provided and non-negative for REMOVE/UPDATE.");
            }

            // Determine which list to validate against
            List<Integer> targetList;
            switch (category) {
                case PERFORMANCE:
                    targetList = hpsDetails.getPerformanceScores();
                    break;
                case QUIZ:
                    targetList = hpsDetails.getQuizScores();
                    break;
                case EXAM:
                    targetList = hpsDetails.getQuarterlyExamScores();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown category: " + category);
            }

            if (index >= targetList.size()) {
                throw new IllegalArgumentException(
                        "Invalid index for " + category + " " + operation +
                                ": " + index + " (valid range is 0 to " + (targetList.size() - 1) + ")"
                );
            }
        }

        switch (category) {
            case PERFORMANCE:
                if (operation == OperationType.ADD) {
                    hpsDetails.getPerformanceScores().add(maxScore);
                }
                else if (operation == OperationType.REMOVE) {
                    hpsDetails.getPerformanceScores().remove((int) index);
                }
                else {
                    hpsDetails.getPerformanceScores().set(index, maxScore);
                }
                break;

            case QUIZ:
                if (operation == OperationType.ADD) {
                    hpsDetails.getQuizScores().add(maxScore);
                }
                else if (operation == OperationType.REMOVE) {
                    hpsDetails.getQuizScores().remove((int) index);
                }
                else {
                    hpsDetails.getQuizScores().set(index, maxScore);
                }
                break;

            case EXAM:
                if (operation == OperationType.ADD) {
                    hpsDetails.getQuarterlyExamScores().add(maxScore);
                }
                else if (operation == OperationType.REMOVE) {
                    hpsDetails.getQuarterlyExamScores().remove((int) index);
                }
                else {
                    hpsDetails.getQuarterlyExamScores().set(index, maxScore);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown category: " + category);
        }

        // 2) Recalculate HPS totals (HPS.ps remains whatever was already set)
        hpsDetails.recalcTotals();

        // 3) Delegate grade propagation to GradeService
        //    You will need a method in GradeService like:
        //    void propagateSlotChange(Long rosterId, String category, String operation, Integer index);
        gradeService.propagateSlotChange(rosterId, category, operation, index);

        // 4) Persist and return updated HPS
        return hpsRepository.save(hps);
    }

    /**
     * Update the PS values on HPS and propagate to all Grades.
     * Delegates actual grade PS→WS→initialGrade logic to GradeService.updateAllGradesPs(...).
     */
    public HighestPossibleScore updatePs(HighestPossibleScoreController.UpdatePsRequest req) {
        Long rosterId = req.getRosterId();

        // 1) Fetch and update only the PS fields (no hardcoding)
        HighestPossibleScore hps = getByRosterId(rosterId);
        var hpsDetails = hps.getScoreDetails();
        hpsDetails.setPerformancePs(req.getPerformancePs());
        hpsDetails.setQuizPs(req.getQuizPs());
        hpsDetails.setQuarterlyExamPs(req.getQuarterlyExamPs());
        // Note: we do NOT override PS to 100.0 here; we trust the frontend value

        // 2) Delegate recalculating each Grade’s PS→WS→initialGrade
        //    You will need a method like:
        //    void updateAllGradesPs(Long rosterId, ScoreDetails hpsDetails);
        gradeService.updateAllGradesPs(rosterId, hps);

        // 3) Save and return updated HPS
        return hpsRepository.save(hps);
    }

    /**
     * Update the WS values on HPS and propagate to all Grades.
     * Delegates actual grade WS→initialGrade logic to GradeService.updateAllGradesWs(...).
     */
    public HighestPossibleScore updateWs(HighestPossibleScoreController.UpdateWsRequest req) {
        Long rosterId = req.getRosterId();

        // 1) Fetch and update only the WS fields
        HighestPossibleScore hps = getByRosterId(rosterId);
        var hpsDetails = hps.getScoreDetails();
        hpsDetails.setPerformanceWs(req.getPerformanceWs());
        hpsDetails.setQuizWs(req.getQuizWs());
        hpsDetails.setQuarterlyExamWs(req.getQuarterlyExamWs());

        // 2) Delegate recalculating each Grade’s WS→initialGrade
        //    You will need a method like:
        //    void updateAllGradesWs(Long rosterId, ScoreDetails hpsDetails);
        gradeService.updateAllGradesWs(rosterId, hps);

        // 3) Save and return updated HPS
        return hpsRepository.save(hps);
    }
}
