// src/components/profile/sections/AcademicSection.tsx

import React from 'react';
import { UseFormRegister, FieldErrors } from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { StudentProfile } from '../types';

interface Props {
  register: UseFormRegister<StudentProfile>;
  errors: FieldErrors<StudentProfile>;
  student: StudentProfile;
  formMode: boolean;
}

export default function AcademicSection({
  register,
  errors,
  student,
  formMode,
}: Props) {
  const today = new Date().toISOString().split('T')[0];

  return (
    <SectionCard id="academic" title="Academic">
      {/* First Attendance Date */}
      <FormField
        label="First Attendance Date"
        error={errors.firstAttendanceDate?.message as string}
        formMode={formMode}
      >
        {formMode ? (
          <input
            type="date"
            max={today}
            {...register('firstAttendanceDate', {
              required: 'First attendance date is required',
              validate: (v) => v <= today || 'Date cannot be in the future',
            })}
            className="block w-full border rounded p-2"
          />
        ) : (
          <p>{student.firstAttendanceDate}</p>
        )}
      </FormField>
    </SectionCard>
  );
}
