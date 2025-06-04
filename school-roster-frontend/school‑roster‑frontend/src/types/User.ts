// src/types/User.ts

/**
 * Matches Java’s UserListResponse DTO:
 * {
 *   "id": string,
 *   "photoUrl": string,
 *   "firstName": string,
 *   "middleName": string,
 *   "lastName": string
 * }
 */
export interface UserListResponse {
  id: string;
  photoUrl: string;
  firstName: string;
  middleName: string;
  lastName: string;
}

/**
 * Matches the Java User entity fields needed for signup.
 * Used in POST /api/auth/signup.
 */
export interface CreateUserRequest {
  email: string;
  password: string;
  roles: string[]; // e.g. ["STUDENT"]
}

/**
 * Matches the Java User return type from signup or whoAmI.
 * { id: string; email: string; roles: string[] }
 */
export interface User {
  id: string;
  email: string;
  roles: string[];
}
