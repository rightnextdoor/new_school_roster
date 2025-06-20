// src/components/profile/mockProfiles.ts

import type { StudentProfile, NonStudentProfile } from './types';
import {
  PhoneType,
  StudentGradeStatus,
  BMICategory,
  Position,
  AppointmentType,
  Role,
  CivilStatus,
  SalaryGrade,
} from './enums';

/** Mock Student Profile */
export const mockStudentProfile: StudentProfile = {
  id: 1001,
  photo: '',
  avatarUrl: '',
  firstName: 'Jane',
  middleName: 'E.',
  lastName: 'Doe',
  email: 'jane.doe@student.edu',
  gender: 'Female',
  birthDate: '2008-04-15',
  address: {
    streetAddress: '123 Main St',
    subdivision: '',
    cityMunicipality: 'Springfield',
    provinceState: 'IL',
    country: 'USA',
    zipCode: '62704',
  },
  phoneNumbers: [{ type: PhoneType.CELLULAR, number: '+11234567890' }],
  role: Role.STUDENT,
  gradeLevel: 8,
  parentMother: { firstName: 'Mary', middleName: 'A.', lastName: 'Doe' },
  parentFather: { firstName: 'John', middleName: 'B.', lastName: 'Doe' },
  nutritional: {
    heightInMeters: 1.45,
    weightInKilograms: 40,
    bmi: 19.0,
    bmiCategory: BMICategory.NORMAL,
  },
  firstAttendanceDate: '2015-09-01',
  schoolHistories: [
    {
      schoolName: 'Springfield Elementary',
      schoolAddress: {
        streetAddress: '742 Evergreen Terrace',
        subdivision: '',
        cityMunicipality: 'Springfield',
        provinceState: 'IL',
        country: 'USA',
        zipCode: '62704',
      },
      gradeLevel: 8,
      sectionNicknames: ['A'],
      schoolYearStart: '2023-08-01',
      schoolYearEnd: '2024-05-31',
      completed: true,
      gpa: 3.8,
      gradeStatus: StudentGradeStatus.PASSED,
    },
  ],
  religion: 'Roman Catholic',
  dialects: ['English', 'Spanish'],
};

/** Mock Non-Student Profile */
export const mockNonStudentProfile: NonStudentProfile = {
  id: 2002,
  photo: '',
  avatarUrl: '',
  firstName: 'Alex',
  middleName: 'C.',
  lastName: 'Smith',
  email: 'alex.smith@school.org',
  gender: 'Male',
  birthDate: '1985-07-20',
  address: {
    streetAddress: '456 Oak Ave',
    subdivision: '',
    cityMunicipality: 'Shelbyville',
    provinceState: 'IL',
    country: 'USA',
    zipCode: '62565',
  },
  phoneNumbers: [{ type: PhoneType.HOME, number: '+11239876543' }],
  role: Role.TEACHER,
  departmentOfEducationEmail: 'doe.teacher@dept.edu',
  gradeLevel: 8,
  civilStatus: CivilStatus.MARRIED,
  spouse: {
    firstName: 'Taylor',
    middleName: '',
    lastName: 'Smith',
    occupation: 'Engineer',
  },
  dependentChildren: [
    {
      firstName: 'Sam',
      middleName: 'J.',
      lastName: 'Smith',
      birthDate: '2012-10-05',
      age: 11,
    },
  ],
  employmentAppointments: [
    {
      appointmentType: AppointmentType.ORIGINAL_APPOINTMENT,
      position: Position.TEACHER_I,
      salaryGrade: SalaryGrade[Position.TEACHER_I],
      salaryAmount: 30000,
      dateIssued: '2010-09-01',
    },
  ],
  educationalBackground: [
    {
      schoolName: 'State University',
      schoolAddress: {
        streetAddress: '789 College Rd',
        subdivision: '',
        cityMunicipality: 'Capital City',
        provinceState: 'IL',
        country: 'USA',
        zipCode: '62701',
      },
      educationLevel: 'MASTERS',
      yearGraduated: '2010-05-15',
    },
  ],
  taxNumber: '',
  gsisNumber: '12345678901',
  philHealthNumber: '987654321',
  pagIbigNumber: '123456789012',
};

/** Mock Administrator Profile */
export const mockAdminProfile: NonStudentProfile = {
  id: 3003,
  photo: '',
  avatarUrl: '',
  firstName: 'Alice',
  middleName: 'M.',
  lastName: 'Administrator',
  email: 'alice.admin@school.org',
  gender: 'Female',
  birthDate: '1978-11-15',
  address: {
    streetAddress: '100 Admin Plaza',
    subdivision: 'Suite 500',
    cityMunicipality: 'Metropolis',
    provinceState: 'IL',
    country: 'USA',
    zipCode: '62960',
  },
  phoneNumbers: [{ type: PhoneType.HOME, number: '+12355501235' }],
  role: Role.ADMINISTRATOR,
  gradeLevel: 0,
  departmentOfEducationEmail: 'admin.dept@school.org',
  civilStatus: CivilStatus.SINGLE,
  spouse: { firstName: '', middleName: '', lastName: '', occupation: '' },
  dependentChildren: [],
  employmentAppointments: [
    {
      appointmentType: AppointmentType.ORIGINAL_APPOINTMENT,
      position: Position.SCHOOL_PRINCIPAL_I,
      salaryGrade: SalaryGrade[Position.SCHOOL_PRINCIPAL_I],
      salaryAmount: 45000,
      dateIssued: '2005-06-15',
    },
  ],
  educationalBackground: [
    {
      schoolName: 'Central State University',
      schoolAddress: {
        streetAddress: '300 College Ave',
        subdivision: '',
        cityMunicipality: 'Capital City',
        provinceState: 'IL',
        country: 'USA',
        zipCode: '62701',
      },
      educationLevel: 'MASTERS',
      yearGraduated: '2002-05-20',
    },
  ],
  taxNumber: '123456789',
  gsisNumber: '12345678901',
  philHealthNumber: '987654321',
  pagIbigNumber: '123456789012',
};
