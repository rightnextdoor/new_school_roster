// src/services/rosterApi.ts
import api from '../api/axios';
import {
  RosterResponse,
  UpdateRosterRequest,
  ReassignTeacherRequest,
  AddStudentRequest,
  IdRequest,
} from '../types/Roster';

/**
 * 1. GET all rosters
 *    GET /api/rosters/getAll
 */
export function getAllRosters() {
  return api.get<RosterResponse[]>('/api/rosters/getAll');
}

/**
 * 2. POST get a roster by ID
 *    POST /api/rosters/getById
 *    Body: { id: number }
 */
export function getRosterById(rosterId: number) {
  const payload: IdRequest = { id: rosterId };
  return api.post<RosterResponse>('/api/rosters/getById', payload);
}

/**
 * 3. POST get logged-in student’s rosters (no body)
 *    POST /api/rosters/getByStudent
 */
export function getRostersByStudent() {
  return api.post<RosterResponse[]>('/api/rosters/getByStudent');
}

/**
 * 4. POST get logged-in teacher’s rosters (no body)
 *    POST /api/rosters/getByTeacher
 */
export function getRostersByTeacher() {
  return api.post<RosterResponse[]>('/api/rosters/getByTeacher');
}

/**
 * 5. POST create a new roster
 *    POST /api/rosters/create
 *    Body: { subjectName, period, nickname, gradeLevel }
 */
export function createRoster(payload: {
  subjectName: string;
  period: string;
  nickname: string;
  gradeLevel: string;
}) {
  return api.post<RosterResponse>('/api/rosters/create', payload);
}

/**
 * 6. PUT update an existing roster
 *    PUT /api/rosters/update
 *    Body: { rosterId: number, updatedRoster: { subjectName, period, nickname, students, classAverageGpa } }
 */
export function updateRoster(payload: UpdateRosterRequest) {
  return api.put<RosterResponse>('/api/rosters/update', payload);
}

/**
 * 7. DELETE a roster
 *    DELETE /api/rosters/delete
 *    Body: { id: number }
 */
export function deleteRoster(rosterId: number) {
  const payload: IdRequest = { id: rosterId };
  console.log('delete roster call');
  return api.delete<void>('/api/rosters/delete', { data: payload });
}

/**
 * 8. POST bulk-add students to a roster
 *    POST /api/rosters/addStudent
 *    Body: { rosterId: number, studentId: string[] }
 */
export function addStudents(payload: AddStudentRequest) {
  return api.post<RosterResponse>('/api/rosters/addStudent', payload);
}

/**
 * 9. POST bulk-remove students from a roster
 *    POST /api/rosters/removeStudent
 *    Body: { rosterId: number, studentId: string[] }
 */
export function removeStudents(payload: AddStudentRequest) {
  return api.post<RosterResponse>('/api/rosters/removeStudent', payload);
}

/**
 * 10. POST reassign a roster’s teacher
 *     POST /api/rosters/reassignTeacher
 *     Body: { rosterId: number, newTeacherId: string }
 */
export function reassignTeacher(payload: ReassignTeacherRequest) {
  return api.post<RosterResponse>('/api/rosters/reassignTeacher', payload);
}
