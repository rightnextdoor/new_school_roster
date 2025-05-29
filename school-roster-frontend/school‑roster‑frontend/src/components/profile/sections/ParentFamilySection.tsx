// src/components/profile/sections/ParentFamilySection.tsx

import React from 'react';
import { UseFormRegister, FieldErrors, useFormContext } from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { StudentProfile } from '../types';

interface Props {
  register: UseFormRegister<StudentProfile>;
  errors: FieldErrors<StudentProfile>;
  formMode: boolean;
}

export default function ParentFamilySection({
  register,
  errors,
  formMode,
}: Props) {
  const { getValues } = useFormContext<StudentProfile>();
  const motherFirstName = getValues('motherFirstName');
  const motherMiddleName = getValues('motherMiddleName');
  const motherMaidenName = getValues('motherMaidenName');
  const fatherFirstName = getValues('fatherFirstName');
  const fatherMiddleName = getValues('fatherMiddleName');
  const fatherLastName = getValues('fatherLastName');

  return (
    <SectionCard id="parent-family" title="Parent & Family">
      {/* Mother Full Name */}
      <div className="grid grid-cols-4 gap-4">
        <FormField
          label="Mother First Name"
          error={errors.motherFirstName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('motherFirstName', {
                required: 'Mother first name is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{motherFirstName}</p>
          )}
        </FormField>
        <FormField label="Mother Middle Name (optional)" formMode={formMode}>
          {formMode ? (
            <input
              type="text"
              {...register('motherMiddleName')}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{motherMiddleName}</p>
          )}
        </FormField>
        <FormField
          label="Mother Maiden Name"
          error={errors.motherMaidenName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('motherMaidenName', {
                required: 'Mother last name is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{motherMaidenName}</p>
          )}
        </FormField>
      </div>

      {/* Father Full Name */}
      <div className="grid grid-cols-4 gap-4 mt-6">
        <FormField
          label="Father First Name"
          error={errors.fatherFirstName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('fatherFirstName', {
                required: 'Father first name is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{fatherFirstName}</p>
          )}
        </FormField>
        <FormField label="Father Middle Name (optional)" formMode={formMode}>
          {formMode ? (
            <input
              type="text"
              {...register('fatherMiddleName')}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{fatherMiddleName}</p>
          )}
        </FormField>
        <FormField
          label="Father Last Name"
          error={errors.fatherLastName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('fatherLastName', {
                required: 'Father last name is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{fatherLastName}</p>
          )}
        </FormField>
      </div>
    </SectionCard>
  );
}
