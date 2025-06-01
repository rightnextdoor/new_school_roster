package com.school.roster.school_roster_backend.entity.embedded;

import jakarta.persistence.ElementCollection;
import lombok.Data;
import jakarta.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

/**
 * Embeddable class to hold score categories and aggregate metrics.
 */
@Embeddable
@Data
public class ScoreDetails {

    @ElementCollection
    private List<Integer> performanceScores = new ArrayList<>();

    @ElementCollection
    private List<Integer> quizScores = new ArrayList<>();

    @ElementCollection
    private List<Integer> quarterlyExamScores = new ArrayList<>();

    private Integer performanceTotal = 0;
    private Integer quizTotal = 0;
    private Integer quarterlyExamTotal = 0;

    private Double performancePs = 0.0;
    private Double quizPs = 0.0;
    private Double quarterlyExamPs = 0.0;

    private Double performanceWs = 0.0;
    private Double quizWs = 0.0;
    private Double quarterlyExamWs = 0.0;

    /**
     * Recalculate totals for each score list.
     */
    public void recalcTotals() {
        this.performanceTotal = sumList(performanceScores);
        this.quizTotal = sumList(quizScores);
        this.quarterlyExamTotal = sumList(quarterlyExamScores);
    }

    /**
     * Clamp this instance's score lists against provided max values.
     * @param hps The HPS ScoreDetails containing max values in its lists.
     */
    public void clampScoresAgainst(ScoreDetails hps) {
        clampList(performanceScores, hps.performanceScores);
        clampList(quizScores, hps.quizScores);
        clampList(quarterlyExamScores, hps.quarterlyExamScores);
    }

    /**
     * Recalculate ps values using HPS totals and HPS ps.
     * @param hps The HPS ScoreDetails containing hpsTotals and hpsPs values.
     */
    public void recalcPs(ScoreDetails hps) {
        this.performancePs = calculatePs(this.performanceTotal, hps.performanceTotal, hps.performancePs);
        this.quizPs = calculatePs(this.quizTotal, hps.quizTotal, hps.quizPs);
        this.quarterlyExamPs = calculatePs(this.quarterlyExamTotal, hps.quarterlyExamTotal, hps.quarterlyExamPs);
    }

    /**
     * Recalculate ws values using this instance's ps and HPS ws.
     * @param hps The HPS ScoreDetails containing hpsWs values.
     */
    public void recalcWs(ScoreDetails hpsDetails) {
        // PERFORMANCE WS = (performancePs × (HPS.performanceWs ÷ 100)), rounded to 2 decimals
        double perfRaw = this.performancePs * (hpsDetails.getPerformanceWs() / 100.0);
        this.performanceWs = Math.round(perfRaw * 100.0) / 100.0;

        // QUIZ WS = (quizPs × (HPS.quizWs ÷ 100)), rounded to 2 decimals
        double quizRaw = this.quizPs * (hpsDetails.getQuizWs() / 100.0);
        this.quizWs = Math.round(quizRaw * 100.0) / 100.0;

        // EXAM WS = (quarterlyExamPs × (HPS.quarterlyExamWs ÷ 100)), rounded to 2 decimals
        double examRaw = this.quarterlyExamPs * (hpsDetails.getQuarterlyExamWs() / 100.0);
        this.quarterlyExamWs = Math.round(examRaw * 100.0) / 100.0;
    }

    /**
     * Helper to sum a list of integers. Empty list returns 0.
     */
    private Integer sumList(List<Integer> list) {
        return list.stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * Clamp each element of 'values' to the corresponding element in 'maxValues'.
     */
    private void clampList(List<Integer> values, List<Integer> maxValues) {
        for (int i = 0; i < values.size() && i < maxValues.size(); i++) {
            int val = values.get(i);
            int max = maxValues.get(i);
            if (val < 0) {
                values.set(i, 0);
            } else if (val > max) {
                values.set(i, max);
            }
        }
    }

    /**
     * Calculate a single ps: (gradeTotal / hpsTotal) * hpsPs, rounded to two decimals.
     */
    private Double calculatePs(Integer gradeTotal, Integer hpsTotal, Double hpsPs) {
        if (hpsTotal == null || hpsTotal == 0) {
            return 0.0;
        }
        double raw = (gradeTotal.doubleValue() / hpsTotal.doubleValue()) * hpsPs;
        return roundToTwoDecimals(raw);
    }

    /**
     * Round a double to two decimal places, half-up.
     */
    private Double roundToTwoDecimals(Double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
