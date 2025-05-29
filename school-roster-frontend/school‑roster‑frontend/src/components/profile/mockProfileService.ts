// src/components/profile/mockProfileService.ts

import type { StudentProfile, NonStudentProfile } from './types';
import {
  mockStudentProfile,
  mockNonStudentProfile,
  mockAdminProfile,
} from './mockProfiles';

/** Four mock scenarios for dev: good vs null, student vs nonstudent */
export enum MockScenario {
  StudentGood,
  StudentNull,
  NonStudentGood,
  NonStudentNull,
  MockAdminGood,
}

let currentScenario = MockScenario.StudentGood;

/** Change which scenario you’re in (for `window.setMockScenario` in dev) */
export function setMockScenario(s: MockScenario) {
  currentScenario = s;
}

/** Read the current scenario (exposed as `window.getMockScenario`) */
export function getMockScenario(): MockScenario {
  return currentScenario;
}

/** Mocked getMyProfile API call */
export function getMyProfile(): Promise<
  StudentProfile | NonStudentProfile | null
> {
  switch (currentScenario) {
    case MockScenario.StudentGood:
      return Promise.resolve(mockStudentProfile);
    case MockScenario.StudentNull:
      return Promise.resolve(null);
    case MockScenario.NonStudentGood:
      return Promise.resolve(mockNonStudentProfile);
    case MockScenario.NonStudentNull:
      return Promise.resolve(null);
    case MockScenario.MockAdminGood:
      return Promise.resolve(mockAdminProfile);
    default:
      return Promise.resolve(null);
  }
}

// Expose in dev console for quick toggling
if (process.env.NODE_ENV === 'development') {
  // @ts-ignore
  window.setMockScenario = setMockScenario;
  // @ts-ignore
  window.getMockScenario = getMockScenario;
}
