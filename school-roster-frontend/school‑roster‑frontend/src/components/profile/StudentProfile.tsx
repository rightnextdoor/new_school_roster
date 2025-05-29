/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/StudentProfile.tsx

import React from 'react';
import type { StudentProfile } from './types';
import SectionCard from './SectionCard';

interface Props {
  data: StudentProfile;
  user: any;
}

const StudentProfile: React.FC<Props> = ({ data, user }) => {
  // Safe defaults for nested objects
  const dialects = data.dialects ?? [];
  const schoolHistories = data.schoolHistories ?? [];
  const birthAddress = data.birthPlace ?? ({} as any);
  const currentAddress = data.address ?? ({} as any);
  const phoneNumbers = data.phoneNumbers ?? [];
  const motherFirstName = data.motherFirstName ?? ({} as any);
  const motherMiddleName = data.motherMiddleName ?? ({} as any);
  const motherMaidenName = data.motherMaidenName ?? ({} as any);
  const fatherFirstName = data.fatherFirstName ?? ({} as any);
  const fatherMiddleName = data.fatherMiddleName ?? ({} as any);
  const fatherLastName = data.fatherLastName ?? ({} as any);
  const nutritional = data.nutritionalStatus ?? ({} as any);

  // Compute age safely
  const age = data.birthDate
    ? Math.floor(
        (Date.now() - new Date(data.birthDate).getTime()) /
          (1000 * 60 * 60 * 24 * 365)
      )
    : 0;

  // Build full name
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
          <strong>Roles:</strong>{' '}
          {Array.isArray(user.roles) ? user.roles.join(', ') : user.roles}
        </p>
        <p>
          <strong>Email:</strong> {user?.email}
        </p>
        <p>
          <strong>Grade Level:</strong> {`Grade ${data.gradeLevel}`}
        </p>
        <p>
          <strong>Gender:</strong> {data.gender}
        </p>
        <p>
          <strong>Date of Birth:</strong> {data.birthDate}
        </p>
        <p>
          <strong>Age:</strong> {age ? `${age} years` : '—'}
        </p>
        <p>
          <strong>Birthplace Address:</strong>{' '}
          {birthAddress.streetAddress || '—'}
          {birthAddress.subdivision ? `, ${birthAddress.subdivision}` : ''}
          {birthAddress.cityMunicipality
            ? `, ${birthAddress.cityMunicipality}`
            : ''}
          {birthAddress.provinceState ? `, ${birthAddress.provinceState}` : ''}
          {birthAddress.country ? `, ${birthAddress.country}` : ''}
          {birthAddress.zipCode ? `, ${birthAddress.zipCode}` : ''}
        </p>
        <p>
          <strong>Religion:</strong> {data.religion}
        </p>
        <p>
          <strong>Dialects:</strong> {dialects.join(', ')}
        </p>
      </SectionCard>

      {/* 2. Contact Info */}
      <SectionCard id="contact-info" title="Contact Info">
        <p>
          <strong>Current Address:</strong>{' '}
          {currentAddress.streetAddress || '—'}
          {currentAddress.subdivision ? `, ${currentAddress.subdivision}` : ''}
          {currentAddress.cityMunicipality
            ? `, ${currentAddress.cityMunicipality}`
            : ''}
          {currentAddress.provinceState
            ? `, ${currentAddress.provinceState}`
            : ''}
          {currentAddress.country ? `, ${currentAddress.country}` : ''}
          {currentAddress.zipCode ? `, ${currentAddress.zipCode}` : ''}
        </p>
        {phoneNumbers.map((phone, i) => (
          <p key={i}>
            <strong>{phone.type}:</strong> {phone.number}
          </p>
        ))}
      </SectionCard>

      {/* 3. Parent & Family */}
      <SectionCard id="parent-family" title="Parent & Family">
        <p>
          <strong>Mother’s Name:</strong> {motherFirstName || '—'}{' '}
          {motherMiddleName || ''} {motherMaidenName || ''}
        </p>
        <p>
          <strong>Father’s Name:</strong> {fatherFirstName || '—'}{' '}
          {fatherMiddleName || ''} {fatherLastName || ''}
        </p>
      </SectionCard>

      {/* 4. Nutritional */}
      <SectionCard id="nutritional" title="Nutritional">
        <p>
          <strong>Height:</strong> {nutritional.heightInMeters || '—'} m
        </p>
        <p>
          <strong>Weight:</strong> {nutritional.weightInKilograms || '—'} kg
        </p>
        <p>
          <strong>BMI:</strong> {nutritional.bmi || '—'}
        </p>
        <p>
          <strong>Category:</strong> {nutritional.bmiCategory || '—'}
        </p>
      </SectionCard>

      {/* 5. Academic */}
      <SectionCard id="academic" title="Academic">
        <p>
          <strong>Grade Level:</strong> {data.gradeLevel || '—'}
        </p>
        <p>
          <strong>First Attendance Date:</strong>{' '}
          {data.firstAttendanceDate || '—'}
        </p>
      </SectionCard>

      {/* 6. School History */}
      <SectionCard id="history" title="School History">
        {schoolHistories.length === 0 && <p>No history available.</p>}
        {schoolHistories.map((h, idx) => (
          <div key={idx} style={{ marginBottom: 12 }}>
            <p>
              <strong>School Name:</strong> {h.schoolName || '—'}
            </p>
            <p>
              <strong>School Address:</strong>{' '}
              {h.schoolAddress.streetAddress || '—'}
              {h.schoolAddress.subdivision
                ? `, ${h.schoolAddress.subdivision}`
                : ''}
              , {h.schoolAddress.cityMunicipality || ''},{' '}
              {h.schoolAddress.provinceState || ''},{' '}
              {h.schoolAddress.country || ''}, {h.schoolAddress.zipCode || ''}
            </p>
            <p>
              <strong>Grade Level:</strong> {h.gradeLevel || '—'}
            </p>
            <p>
              <strong>School Years:</strong> {h.schoolYearStart || '—'} –{' '}
              {h.schoolYearEnd || '—'}
            </p>
            <p>
              <strong>Section Nicknames:</strong> {h.sectionNicknames || '—'}
            </p>
            <p>
              <strong>Completed:</strong> {h.completed ? 'Yes' : 'No'}
            </p>
            <p>
              <strong>GPA:</strong> {h.gpa || '—'}
            </p>
            <p>
              <strong>Status:</strong> {h.gradeStatus || '—'}
            </p>
            {idx < schoolHistories.length - 1 && <hr />}
          </div>
        ))}
      </SectionCard>

      {/* 7. Account Settings */}
      <SectionCard id="account-settings" title="Account Settings">
        <button
          className="teal-button"
          onClick={() =>
            alert('Your teacher has been notified to change your password.')
          }
        >
          Request Password Change
        </button>
      </SectionCard>
    </>
  );
};

export default StudentProfile;
