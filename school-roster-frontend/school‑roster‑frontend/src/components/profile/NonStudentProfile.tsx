/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/NonStudentProfile.tsx

import React from 'react';
import type { NonStudentProfile } from './types';
import SectionCard from './SectionCard';

interface Props {
  data: NonStudentProfile;
  user: any;
}

const mask = (val: string) => `••••-${val.slice(-4)}`;

export default function NonStudentProfile({ data, user }: Props) {
  // derive role from user.roles array
  const roles: string[] = Array.isArray(user?.roles) ? user.roles : [];
  const primaryRole = roles[0] || '';
  const roleName = primaryRole
    .split('_')
    .map((w) => w.charAt(0) + w.slice(1).toLowerCase())
    .join(' ');

  const phoneNumbers = data.phoneNumbers ?? [];
  const dependentChildren = data.dependentChildren ?? [];
  const employmentAppointments = data.employmentAppointments ?? [];
  const educationalBackground = data.educationalBackground ?? [];

  // Addresses
  const birthAddress = data.birthPlace;
  const currentAddress = data.address;

  // Compute age
  const age = Math.floor(
    (Date.now() - new Date(data.birthDate).getTime()) /
      (1000 * 60 * 60 * 24 * 365)
  );

  // Full name
  const fullName = [data.firstName, data.middleName, data.lastName]
    .filter(Boolean)
    .join(' ');

  return (
    <>
      {/* 1. User Info */}
      <SectionCard id="user-info" title="User Info">
        <p>
          <strong>User ID:</strong> {user?.id}
        </p>
        <p>
          <strong>Full Name:</strong> {fullName}
        </p>
        <p>
          <strong>Role:</strong> {roleName}
        </p>
        {data.gradeLevel !== undefined && (
          <p>
            <strong>Grade Level:</strong> Grade {data.gradeLevel}
          </p>
        )}
        <p>
          <strong>Email:</strong> {user?.email}
        </p>
        <p>
          <strong>Gender:</strong> {data.gender}
        </p>
        <p>
          <strong>Date of Birth:</strong> {data.birthDate}
        </p>
        <p>
          <strong>Age:</strong> {age} years
        </p>
        <p>
          <strong>Birthplace Address:</strong>{' '}
          {birthAddress
            ? `${birthAddress.streetAddress}${
                birthAddress.subdivision ? `, ${birthAddress.subdivision}` : ''
              }, ${birthAddress.cityMunicipality}, ${
                birthAddress.provinceState
              }, ${birthAddress.country}, ${birthAddress.zipCode}`
            : '—'}
        </p>
      </SectionCard>

      {/* 2. Contact & Department of Education */}
      <SectionCard id="contact-doe" title="Contact & Department of Education">
        <p>
          <strong>Current Address:</strong>{' '}
          {currentAddress
            ? `${currentAddress.streetAddress}${
                currentAddress.subdivision
                  ? `, ${currentAddress.subdivision}`
                  : ''
              }, ${currentAddress.cityMunicipality}, ${
                currentAddress.provinceState
              }, ${currentAddress.country}, ${currentAddress.zipCode}`
            : '—'}
        </p>
        {phoneNumbers.map((p, i) => (
          <p key={i}>
            <strong>{p.type}:</strong> {p.number}
          </p>
        ))}
        {data.departmentOfEducationEmail && (
          <p>
            <strong>Department of Education Email:</strong>{' '}
            {data.departmentOfEducationEmail}
          </p>
        )}
      </SectionCard>

      {/* 3. Civil Status & Family */}
      <SectionCard id="civil-family" title="Civil Status & Family">
        {data.civilStatus && (
          <p>
            <strong>Status:</strong> {data.civilStatus}
          </p>
        )}
        {data.spouseFirstName && (
          <>
            <p>
              <strong>Spouse Full Name:</strong>{' '}
              {[
                data.spouseFirstName,
                data.spouseMiddleName,
                data.spouseLastName,
              ]
                .filter(Boolean)
                .join(' ')}
            </p>
            {data.spouseOccupation && (
              <p>
                <strong>Occupation:</strong> {data.spouseOccupation}
              </p>
            )}
          </>
        )}
      </SectionCard>

      {/* 4. Dependent Children */}
      {dependentChildren.length > 0 && (
        <SectionCard id="dependents" title="Dependent Children">
          {dependentChildren.map((c, i) => (
            <div key={i} style={{ marginBottom: 12 }}>
              <p>
                <strong>Full Name:</strong>{' '}
                {[c.firstName, c.middleName, c.lastName]
                  .filter(Boolean)
                  .join(' ')}
              </p>
              <p>
                <strong>Date of Birth:</strong> {c.birthDate}
              </p>
              <p>
                <strong>Age:</strong> {c.age} years
              </p>
              {i < dependentChildren.length - 1 && <hr />}
            </div>
          ))}
        </SectionCard>
      )}

      {/* 5. Career & Appointments */}
      <SectionCard id="career" title="Career & Appointments">
        {employmentAppointments.map((e, i) => (
          <div key={i} style={{ marginBottom: 12 }}>
            <p>
              <strong>Appointment Type:</strong> {e.appointmentType}
            </p>
            <p>
              <strong>Position:</strong> {e.position}
            </p>
            <p>
              <strong>Salary Grade:</strong> {e.salaryGrade}
            </p>
            <p>
              <strong>Salary Amount:</strong> {e.salaryAmount}
            </p>
            <p>
              <strong>Date Issued:</strong> {e.dateIssued}
            </p>
            {i < employmentAppointments.length - 1 && <hr />}
          </div>
        ))}
      </SectionCard>

      {/* 6. Educational Background */}
      <SectionCard id="education" title="Educational Background">
        {educationalBackground.map((e, i) => (
          <div key={i} style={{ marginBottom: 12 }}>
            <p>
              <strong>School Name:</strong> {e.schoolName}
            </p>
            <p>
              <strong>School Address:</strong> {e.schoolAddress.streetAddress},{' '}
              {e.schoolAddress.subdivision
                ? `${e.schoolAddress.subdivision}, `
                : ''}
              {`${e.schoolAddress.cityMunicipality}, ${e.schoolAddress.provinceState}, ${e.schoolAddress.country}, ${e.schoolAddress.zipCode}`}
            </p>
            <p>
              <strong>Education Level:</strong> {e.educationLevel}
            </p>
            <p>
              <strong>Year Graduated:</strong> {e.yearGraduated}
            </p>
            {i < educationalBackground.length - 1 && <hr />}
          </div>
        ))}
      </SectionCard>

      {/* 7. Government IDs */}
      <SectionCard id="gov-ids" title="Government IDs">
        <p>
          <strong>Tax Number:</strong>{' '}
          {data.taxNumberEncrypted ? mask(data.taxNumberEncrypted) : '—'}
        </p>
        <p>
          <strong>GSIS Number:</strong>{' '}
          {data.gsisNumberEncrypted ? mask(data.gsisNumberEncrypted) : '—'}
        </p>
        <p>
          <strong>PhilHealth Number:</strong>{' '}
          {data.philHealthNumberEncrypted
            ? mask(data.philHealthNumberEncrypted)
            : '—'}
        </p>
        <p>
          <strong>Pag-IBIG Number:</strong>{' '}
          {data.pagIbigNumberEncrypted
            ? mask(data.pagIbigNumberEncrypted)
            : '—'}
        </p>
      </SectionCard>

      {/* 8. Account Settings */}
      <SectionCard id="account-settings" title="Account Settings">
        <button
          className="teal-button"
          onClick={() => alert('Navigating to profile edit mode...')}
        >
          Change Password
        </button>
      </SectionCard>
    </>
  );
}
