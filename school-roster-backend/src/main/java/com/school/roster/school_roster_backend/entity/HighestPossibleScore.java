package com.school.roster.school_roster_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.school.roster.school_roster_backend.entity.embedded.ScoreDetails;
import lombok.Data;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing the Highest Possible Score for a Roster.
 * Automatically initialized with one default slot per category.
 */
@Entity
@Table(name = "highest_possible_score")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class HighestPossibleScore {

    @Id
    @Column(name = "roster_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "roster_id")
    @JsonBackReference(value = "roster-hps")
    private Roster roster;

    @Embedded
    private ScoreDetails scoreDetails = new ScoreDetails();

    public HighestPossibleScore() {
        // Default constructor
    }

    /**
     * Initialize default values before first persist:
     * - Ensure one slot (0) in each score list
     * - Set ps to 100 for each category
     * - Set default ws weights (40, 40, 20)
     */
    @PrePersist
    private void initializeDefaults() {
        // If lists are empty, add one slot of zero
        if (scoreDetails.getPerformanceScores().isEmpty()) {
            scoreDetails.getPerformanceScores().add(0);
        }
        if (scoreDetails.getQuizScores().isEmpty()) {
            scoreDetails.getQuizScores().add(0);
        }
        if (scoreDetails.getQuarterlyExamScores().isEmpty()) {
            scoreDetails.getQuarterlyExamScores().add(0);
        }

        // Totals are already zero by default; set ps to 100.0
        scoreDetails.setPerformancePs(100.0);
        scoreDetails.setQuizPs(100.0);
        scoreDetails.setQuarterlyExamPs(100.0);

        // Set default ws weights
        scoreDetails.setPerformanceWs(40.0);
        scoreDetails.setQuizWs(40.0);
        scoreDetails.setQuarterlyExamWs(20.0);

        // Recalculate totals in case lists were just populated
        scoreDetails.recalcTotals();
    }
}