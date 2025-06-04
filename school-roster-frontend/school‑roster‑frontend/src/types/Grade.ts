// src/types/Grade.ts

/**
 * Represents one student’s full grade record for a given roster.
 */
export interface GradeDto {
  // ─── Basic identifiers
  gradeId: number;
  initialGrade: number;
  finalStatus: string;

  // ─── Student summary
  studentId: string;
  studentFirstName: string;
  studentLastName: string;

  // ─── Roster summary
  rosterId: number;
  subjectName: string;
  period: string;
  nickname: string;
  teacherFirstName: string;
  teacherLastName: string;

  // ─── PERFORMANCE breakdown
  performanceScores: number[];
  performanceTotal: number;
  performancePs: number;
  performanceWs: number;

  // ─── QUIZ breakdown
  quizScores: number[];
  quizTotal: number;
  quizPs: number;
  quizWs: number;

  // ─── EXAM breakdown
  quarterlyExamScores: number[];
  quarterlyExamTotal: number;
  quarterlyExamPs: number;
  quarterlyExamWs: number;
}

/**
 * Returned by GET /api/grades/myGpa
 */
export interface GpaDto {
  gpa: number;
}
