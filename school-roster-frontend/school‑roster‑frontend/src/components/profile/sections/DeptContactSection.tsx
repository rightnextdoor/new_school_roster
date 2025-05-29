// src/components/profile/sections/DeptContactSection.tsx

import React from 'react';
import { UseFormRegister, FieldErrors, useFormContext } from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { NonStudentProfile } from '../types';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  formMode: boolean;
}

export default function DeptContactSection({
  register,
  errors,
  formMode,
}: Props) {
  const { getValues } = useFormContext<NonStudentProfile>();

  return (
    <SectionCard id="dept-contact" title="Department of Education Contact">
      <FormField
        label="Department of Education Email"
        error={errors.departmentOfEducationEmail?.message}
        formMode={formMode}
      >
        {formMode ? (
          <input
            type="email"
            placeholder="example@deped.gov"
            {...register('departmentOfEducationEmail', {
              required: 'DOE email is required',
              pattern: {
                value: /^\S+@\S+\.[\S]+$/,
                message: 'Enter a valid email',
              },
            })}
            className="block w-full border rounded p-2"
          />
        ) : (
          <p>{getValues('departmentOfEducationEmail')}</p>
        )}
      </FormField>
    </SectionCard>
  );
}
