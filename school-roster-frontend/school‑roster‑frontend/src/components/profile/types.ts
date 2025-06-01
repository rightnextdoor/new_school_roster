// src/components/profile/types.ts

import {
  Position,
  AppointmentType,
  BMICategory,
  CivilStatus,
  PhoneType,
  StudentGradeStatus,
} from './enums';

export interface Address {
  streetAddress: string;
  subdivision?: string;
  cityMunicipality: string;
  provinceState: string;
  country: string;
  zipCode: string;
}

export interface PhoneNumberEntry {
  type: PhoneType;
  number: string;
}

export interface DependentChild {
  firstName: string;
  middleName?: string;
  lastName: string;
  birthDate: string;
  age: number;
}

export interface NutritionalStatus {
  heightInCentimeters: number;
  weightInKilograms: number;
  bmi: number;
  bmiCategory: BMICategory;
}

export interface AppointmentRecord {
  appointmentType: AppointmentType;
  position: Position;
  salaryGrade: number;
  salaryAmount: number;
  dateIssued: string;
}

export interface EducationRecord {
  schoolName: string;
  schoolAddress: Address;
  educationLevel: string;
  yearGraduated: number;
}

export interface SchoolYearHistory {
  schoolName: string;
  schoolAddress: Address;
  gradeLevel: string;
  sectionNicknames: string;
  schoolYearStart: string;
  schoolYearEnd: string;
  completed: boolean;
  gpa: number;
  gradeStatus: StudentGradeStatus;
}

export interface StudentProfile {
  id: number;
  linkedUser?: { id: string };
  profilePicture?: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  gender: string;
  birthDate: string;
  birthPlace: Address;
  address: Address;
  religion?: string;
  dialects?: string[];
  phoneNumbers: PhoneNumberEntry[];
  motherFirstName: string;
  motherMiddleName?: string;
  motherMaidenName: string;
  fatherFirstName: string;
  fatherMiddleName?: string;
  fatherLastName: string;
  nutritionalStatus: NutritionalStatus;
  gradeLevel: number;
  firstAttendanceDate: string;
  schoolHistories: SchoolYearHistory[];
}

export interface NonStudentProfile {
  id: number;
  linkedUser?: { id: string };
  profilePicture?: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  email: string;
  gender: string;
  birthDate: string;
  birthPlace: Address;
  address: Address;
  phoneNumbers: PhoneNumberEntry[];
  departmentOfEducationEmail?: string;
  gradeLevel?: number;
  civilStatus?: CivilStatus;
  spouseFirstName?: string;
  spouseMiddleName?: string;
  spouseLastName?: string;
  spouseOccupation?: string;
  dependentChildren?: DependentChild[];
  employmentAppointments?: AppointmentRecord[];
  educationalBackground?: EducationRecord[];
  taxNumberEncrypted?: string;
  gsisNumberEncrypted?: string;
  philHealthNumberEncrypted?: string;
  pagIbigNumberEncrypted?: string;
}
