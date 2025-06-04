// src/components/profile/ProfileForm.tsx

import React from 'react';
import type { StudentProfile, NonStudentProfile } from './types';
import StudentProfileForm from './StudentProfileForm';
import NonStudentProfileForm from './NonStudentProfileForm';
import { Role } from './enums';

interface Props {
  data: StudentProfile | NonStudentProfile;
  mode: 'edit' | 'create';
  onSave: (data: StudentProfile | NonStudentProfile) => void;
  onCancel: () => void;
  saving?: boolean;
  saveError?: string | null;
  user: {
    id: string;
    email: string;
    roles: string[];
  };
}

export default function ProfileForm({
  data,
  mode,
  onSave,
  onCancel,
  saving = false,
  saveError = null,
  user,
}: Props) {
  const roles = Array.isArray(user.roles) ? user.roles : [];
  const isStudent = roles.includes(Role.STUDENT);

  // 1) Intercept “Enter” at the container level:
  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      // Prevent any <form> from submitting on Enter
      e.preventDefault();
    }
  };

  // 2) Before calling onSave, ensure that photo is not empty.
  //    We assume both StudentProfileForm and NonStudentProfileForm
  //    supply a “profilePicture” field on `data` (even if it’s an empty string).
  const handleSaveStudent = (studentData: StudentProfile) => {
    if (!studentData.profilePicture) {
      alert('Photo cannot be empty.');
      return;
    }
    onSave(studentData);
  };
  const handleSaveNonStudent = (nonStudentData: NonStudentProfile) => {
    if (!nonStudentData.profilePicture) {
      alert('Photo cannot be empty.');
      return;
    }
    onSave(nonStudentData);
  };

  return (
    <div className="profile-form-container" onKeyDown={handleKeyDown}>
      {saveError && <div className="error-banner">{saveError}</div>}

      {isStudent ? (
        <StudentProfileForm
          student={data as StudentProfile}
          user={user}
          roles={roles}
          mode={mode}
          onSave={handleSaveStudent}
          onCancel={onCancel}
          saving={saving}
        />
      ) : (
        <NonStudentProfileForm
          nonStudent={data as NonStudentProfile}
          user={user}
          roles={roles}
          mode={mode}
          onSave={handleSaveNonStudent}
          onCancel={onCancel}
          saving={saving}
        />
      )}
    </div>
  );
}
