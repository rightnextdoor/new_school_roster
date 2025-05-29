/* eslint-disable @typescript-eslint/no-explicit-any */
// src/services/profileApi.ts

import api from '../api/axios';
import type {
  StudentProfile,
  NonStudentProfile,
} from '../components/profile/types';
import { decryptField } from '../utils/crypto';
import type { StudentListItem } from '../components/students/types';

/**
 * Fetch the current user’s profile (student or non-student).
 * Returns `null` if no profile exists yet.
 * Decrypts any government ID fields for UI use.
 */
export async function getMyProfile(): Promise<
  StudentProfile | NonStudentProfile | null
> {
  const resp = await api.get<any>('/api/profiles/getMyProfile');
  const raw = resp.data;

  if (raw === '' || raw == null) {
    return null;
  }

  // If this is a NonStudentProfile, decrypt any non-empty gov ID fields
  const ns = raw as NonStudentProfile;
  (
    [
      'taxNumberEncrypted',
      'gsisNumberEncrypted',
      'philHealthNumberEncrypted',
      'pagIbigNumberEncrypted',
    ] as const
  ).forEach((field) => {
    const val = (ns as any)[field];
    if (typeof val === 'string' && val.length > 0) {
      try {
        const decrypted = decryptField(val);
        (ns as any)[field] = decrypted;
      } catch (de) {
        console.error(`Error decrypting field ${field}:`, de);
      }
    }
  });

  return raw as StudentProfile | NonStudentProfile;
}

/**
 * Update an existing student profile.
 */
export async function updateStudentProfile(
  profileId: number,
  updatedProfile: StudentProfile
): Promise<StudentProfile> {
  const resp = await api.put<StudentProfile>('/api/profiles/student/update', {
    profileId,
    updatedProfile,
  });
  return resp.data;
}

/**
 * Update an existing non-student profile.
 */
export async function updateNonStudentProfile(
  profileId: number,
  updatedProfile: NonStudentProfile
): Promise<NonStudentProfile> {
  const resp = await api.put<NonStudentProfile>(
    '/api/profiles/nonstudent/update',
    {
      profileId,
      updatedProfile,
    }
  );
  return resp.data;
}

/**
 * Create a new student profile for the given user ID.
 */
export async function createStudentProfile(
  userId: string,
  studentProfile: StudentProfile
): Promise<StudentProfile> {
  const resp = await api.post<StudentProfile>('/api/profiles/student/create', {
    userId,
    studentProfile,
  });
  return resp.data;
}

/**
 * Create a new non-student profile for the given user ID.
 */
export async function createNonStudentProfile(
  userId: string,
  nonStudentProfile: NonStudentProfile
): Promise<NonStudentProfile> {
  const resp = await api.post<NonStudentProfile>(
    '/api/profiles/nonstudent/create',
    {
      userId,
      nonStudentProfile,
    }
  );
  return resp.data;
}

/**
 * Fetch the full list of students for the admin/teacher list page.
 */
export function getStudentList(): Promise<StudentListItem[]> {
  return api
    .get<StudentListItem[]>('/api/profiles/student/list')
    .then((res) => res.data);
}

/**
 * Fetch a single user’s profile (student or non-student) by their user ID.
 * Returns null if no profile exists.
 */
export async function getProfileByUserId(
  userId: string
): Promise<StudentProfile | NonStudentProfile | null> {
  // POST to the /api/profiles/getByUser endpoint (per your backend controller)
  const resp = await api.post<any>('/api/profiles/getByUser', { userId });
  const raw = resp.data;

  if (raw === '' || raw == null) {
    return null;
  }
  console.log('get profile by id ', raw);
  // If this is a NonStudentProfile, decrypt any non-empty gov ID fields
  const ns = raw as NonStudentProfile;
  (
    [
      'taxNumberEncrypted',
      'gsisNumberEncrypted',
      'philHealthNumberEncrypted',
      'pagIbigNumberEncrypted',
    ] as const
  ).forEach((field) => {
    const val = (ns as any)[field];
    if (typeof val === 'string' && val.length > 0) {
      try {
        const decrypted = decryptField(val);
        (ns as any)[field] = decrypted;
      } catch (de) {
        console.error(`Error decrypting field ${field}:`, de);
      }
    }
  });

  return raw as StudentProfile | NonStudentProfile;
}
