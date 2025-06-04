// src/services/userApi.ts
import api from '../api/axios';
import { UserListResponse, CreateUserRequest, User } from '../types/User';

/**
 * 1. POST /api/auth/signup
 *    Body: { email: string, password: string, roles: string[] }
 *    Returns the created User { id, email, roles }
 */
export function createUser(payload: CreateUserRequest) {
  return api.post<User>('/api/auth/signup', payload);
}

/**
 * 2. POST /api/users/get
 *    Body: { id: string }
 *    Returns User { id, email, roles }
 */
export function getUserById(userId: string) {
  return api
    .post<User>('/api/users/get', { id: userId })
    .then((res) => res.data);
}

/**
 * 3. POST /api/users/roleList  (formerly “by-role”)
 *    Body: { role: 'TEACHER' }
 *    Returns List<UserListResponse>
 */
export function getAllTeachers() {
  return api.post<UserListResponse[]>('/api/users/roleList', {
    role: 'TEACHER',
  });
}

/**
 * 4. POST /api/users/roleList
 *    Body: { role: 'STUDENT' }
 *    Returns List<UserListResponse>
 */
export function getAllStudents() {
  return api.post<UserListResponse[]>('/api/users/roleList', {
    role: 'STUDENT',
  });
}

// If you need to re-export the `User` interface, use `export type`:
export type { User };
