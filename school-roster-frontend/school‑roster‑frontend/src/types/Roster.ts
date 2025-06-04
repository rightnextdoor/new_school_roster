// src/types/Roster.ts

/**
 * Request payload to update a roster.
 * Matches Java’s UpdateRosterRequest:
 * {
 *   "rosterId": number,
 *   "updatedRoster": {
 *     "subjectName": string,
 *     "period": string,
 *     "nickname": string,
 *     "students": string[],
 *     "classAverageGpa": number
 *   }
 * }
 */
export interface UpdateRosterRequest {
  rosterId: number;
  updatedRoster: {
    subjectName: string;
    period: string;
    nickname: string;
    gradeLevel: string;
  };
}

/**
 * Request payload to reassign a roster’s teacher.
 * Matches Java’s ReassignTeacherRequest:
 * {
 *   "rosterId": number,
 *   "newTeacherId": string
 * }
 */
export interface ReassignTeacherRequest {
  rosterId: number;
  newTeacherId: string;
}

/**
 * Response payload for a roster.
 * Matches Java’s RosterResponse:
 * {
 *   "rosterId": number,
 *   "subjectName": string,
 *   "period": string,
 *   "nickname": string,
 *   "gradeLevel": string,
 *   "teacherFirstName": string,
 *   "teacherMiddleName": string,
 *   "teacherLastName": string,
 *   "students": StudentInfo[],
 *   "classGpa": number
 * }
 */
export interface RosterResponse {
  rosterId: number;
  subjectName: string;
  period: string;
  nickname: string;
  gradeLevel: string;
  teacherId: string;
  teacherFirstName: string;
  teacherMiddleName: string;
  teacherLastName: string;
  teacherPhoto: string;
  students: StudentInfo[];
  classGpa: number;
}

/**
 * Information for a single student within a roster.
 * Matches Java’s StudentInfo:
 * {
 *   "studentId": string,
 *   "firstName": string,
 *   "middleName": string,
 *   "lastName": string,
 *   "finalGpa": number,
 *   "finalStatus": string
 * }
 */
export interface StudentInfo {
  studentId: string;
  studentPhoto: string;
  firstName: string;
  middleName: string;
  lastName: string;
  finalGpa: number;
  finalStatus: string;
}

/**
 * Generic “ID” request used for endpoints like getById.
 * Matches Java’s IdRequest:
 * {
 *   "id": number
 * }
 */
export interface IdRequest {
  id: number;
}

/**
 * Request payload to add students to a roster.
 * Matches Java’s AddStudentRequest:
 * {
 *   "rosterId": number,
 *   "studentId": string[]   // note: plural, even though Java field is studentId
 * }
 */
export interface AddStudentRequest {
  rosterId: number;
  studentId: string[];
}
