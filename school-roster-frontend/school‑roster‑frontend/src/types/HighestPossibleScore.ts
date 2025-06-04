// src/types/HighestPossibleScore.ts

/**
 * Matches the backend’s HighestPossibleScore entity, including embedded ScoreDetails.
 */
export interface HighestPossibleScore {
  // Unique identifier for this HPS record
  id: number;

  // The roster to which these HPS values apply
  rosterId: number;

  // ─── PERFORMANCE breakdown ────────────────────────────────────────
  performanceScores: number[]; // List of max-scores for each performance task slot
  performanceTotal: number; // Sum of all performanceScores
  performancePs: number; // Percentage split for performance
  performanceWs: number; // Weight split for performance

  // ─── QUIZ (Written Works) breakdown ───────────────────────────────
  quizScores: number[]; // List of max-scores for each quiz slot
  quizTotal: number; // Sum of all quizScores
  quizPs: number; // Percentage split for quizzes
  quizWs: number; // Weight split for quizzes

  // ─── EXAM (Quarterly Assessment) breakdown ───────────────────────
  quarterlyExamScores: number[]; // List of max-scores for each exam slot
  quarterlyExamTotal: number; // Sum of all quarterlyExamScores
  quarterlyExamPs: number; // Percentage split for exams
  quarterlyExamWs: number; // Weight split for exams
}
