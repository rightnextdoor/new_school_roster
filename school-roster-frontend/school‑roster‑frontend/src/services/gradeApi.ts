// src/services/gradeApi.ts
import api from '../api/axios';
import { GradeDto, GpaDto } from '../types/Grade';

/**
 * 1. POST get all grades for a roster
 *    POST /api/grades/getByRoster
 *    Body: { rosterId: number }
 */
export function getGradesByRoster(rosterId: number) {
  return api.post<GradeDto[]>('/api/grades/getByRoster', { rosterId });
}

/**
 * 2. POST get all grades for the logged-in student
 *    POST /api/grades/getByStudent
 *    (no body; backend infers from authentication)
 */
export function getGradesByStudent() {
  return api.post<GradeDto[]>('/api/grades/getByStudent');
}

/**
 * 3. GET get the logged-in student’s GPA
 *    GET /api/grades/myGpa
 */
export function getMyGpa() {
  return api.get<GpaDto>('/api/grades/myGpa');
}

/**
 * 4. PUT update an entire grade record’s scores
 *    PUT /api/grades/update
 *    Body: {
 *      rosterId: number,
 *      gradeId: number,
 *      performanceScores: number[],
 *      quizScores: number[],
 *      quarterlyExamScores: number[]
 *    }
 */
export function updateGradeRecord(payload: {
  rosterId: number;
  gradeId: number;
  performanceScores: number[];
  quizScores: number[];
  quarterlyExamScores: number[];
}) {
  return api.put<GradeDto>('/api/grades/update', payload);
}

/**
 * 5. DELETE remove a grade record
 *    DELETE /api/grades/delete
 *    Body: { id: number }
 */
export function deleteGrade(gradeId: number) {
  return api.delete<void>('/api/grades/delete', { data: { id: gradeId } });
}
