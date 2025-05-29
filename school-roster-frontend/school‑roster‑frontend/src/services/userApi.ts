// src/services/userApi.ts

import api from '../api/axios';

/**
 * User DTO matching backend's User entity projection.
 */
export interface User {
  id: string;
  email: string;
  roles: string[];
}

export function getUserById(userId: string): Promise<User> {
  return api
    .post<User>('/api/users/get', { id: userId })
    .then((res) => res.data);
}
