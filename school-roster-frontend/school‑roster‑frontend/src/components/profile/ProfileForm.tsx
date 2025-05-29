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

  // Wrap onSave to match each form’s expected signature
  const handleSaveStudent = (studentData: StudentProfile) => {
    onSave(studentData);
  };
  const handleSaveNonStudent = (nonStudentData: NonStudentProfile) => {
    onSave(nonStudentData);
  };

  return (
    <div className="profile-form-container">
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
