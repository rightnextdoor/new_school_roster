// src/components/profile/sections/CivilStatusFamilySection.tsx

import React from 'react';
import {
  useFormContext,
  useWatch,
  Control,
  UseFormRegister,
  FieldErrors,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { NonStudentProfile } from '../types';
import { CivilStatus } from '../enums';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  control: Control<NonStudentProfile>;
  formMode: boolean;
}

export default function CivilStatusFamilySection({
  register,
  errors,
  control,
  formMode,
}: Props) {
  // Watch the civil status to determine if spouse fields should show
  const civilStatus = useWatch({ control, name: 'civilStatus' });
  const isMarried = civilStatus === CivilStatus.MARRIED;
  const { getValues } = useFormContext<NonStudentProfile>();

  // For read-only view
  const spouseFirst = getValues('spouseFirstName');
  const spouseMiddle = getValues('spouseMiddleName');
  const spouseLast = getValues('spouseLastName');
  const spouseOcc = getValues('spouseOccupation');

  return (
    <SectionCard id="civil-family" title="Civil Status & Family">
      {/* Civil Status */}
      <FormField
        label="Civil Status"
        error={errors.civilStatus?.message as string}
        formMode={formMode}
      >
        {formMode ? (
          <select
            {...register('civilStatus', {
              required: 'Civil status is required',
            })}
            className="block w-full border rounded p-2"
          >
            <option value="">Select status</option>
            {Object.values(CivilStatus).map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        ) : (
          <p>{civilStatus}</p>
        )}
      </FormField>

      {/* Spouse fields only when married */}
      {isMarried && (
        <>
          <div className="grid grid-cols-4 gap-4">
            <FormField
              label="Spouse First Name"
              error={errors.spouseFirstName?.message as string}
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="text"
                  {...register('spouseFirstName', {
                    required: 'Spouse first name is required',
                  })}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{spouseFirst}</p>
              )}
            </FormField>

            <FormField label="Spouse Middle Name" formMode={formMode}>
              {formMode ? (
                <input
                  type="text"
                  {...register('spouseMiddleName')}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{spouseMiddle}</p>
              )}
            </FormField>

            <FormField
              label="Spouse Last Name"
              error={errors.spouseLastName?.message as string}
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="text"
                  {...register('spouseLastName', {
                    required: 'Spouse last name is required',
                  })}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{spouseLast}</p>
              )}
            </FormField>

            <FormField
              label="Spouse Occupation"
              error={errors.spouseOccupation?.message as string}
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="text"
                  {...register('spouseOccupation', {
                    required: 'Spouse occupation is required',
                  })}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{spouseOcc}</p>
              )}
            </FormField>
          </div>
        </>
      )}
    </SectionCard>
  );
}
