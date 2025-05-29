// src/components/profile/sections/DependentChildrenSection.tsx

import React from 'react';
import {
  useFieldArray,
  Control,
  UseFormRegister,
  FieldErrors,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { NonStudentProfile } from '../types';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  control: Control<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  formMode: boolean;
}

export default function DependentChildrenSection({
  register,
  control,
  errors,
  formMode,
}: Props) {
  const { fields, append, remove } = useFieldArray<
    NonStudentProfile,
    'dependentChildren'
  >({ control, name: 'dependentChildren' });

  const today = new Date().toISOString().split('T')[0];

  return (
    <SectionCard id="dependents" title="Dependent Children">
      {fields.map((child, index) => (
        <div key={child.id} className="mb-6">
          {/* Row 1: Full Name */}
          <div className="grid grid-cols-4 gap-4">
            <FormField
              label="First Name"
              error={
                errors.dependentChildren?.[index]?.firstName?.message as string
              }
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="text"
                  {...register(
                    `dependentChildren.${index}.firstName` as const,
                    {
                      required: 'Child first name is required',
                      minLength: { value: 2, message: 'Too short' },
                    }
                  )}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{child.firstName}</p>
              )}
            </FormField>

            <FormField label="Middle Name (optional)" formMode={formMode}>
              {formMode ? (
                <input
                  type="text"
                  {...register(
                    `dependentChildren.${index}.middleName` as const
                  )}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{child.middleName}</p>
              )}
            </FormField>

            <FormField
              label="Last Name"
              error={
                errors.dependentChildren?.[index]?.lastName?.message as string
              }
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="text"
                  {...register(`dependentChildren.${index}.lastName` as const, {
                    required: 'Child last name is required',
                    minLength: { value: 2, message: 'Too short' },
                  })}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{child.lastName}</p>
              )}
            </FormField>
          </div>

          {/* Row 2: Date of Birth & Age */}
          <div className="grid grid-cols-4 gap-4 mt-4">
            <FormField
              label="Date of Birth"
              error={
                errors.dependentChildren?.[index]?.birthDate?.message as string
              }
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="date"
                  max={today}
                  {...register(
                    `dependentChildren.${index}.birthDate` as const,
                    {
                      required: 'Date of birth is required',
                      validate: (v) =>
                        v <= today || 'Date cannot be in the future',
                    }
                  )}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{child.birthDate}</p>
              )}
            </FormField>

            <FormField label="Age" formMode={formMode}>
              {formMode ? (
                <input
                  type="number"
                  {...register(`dependentChildren.${index}.age` as const, {
                    valueAsNumber: true,
                  })}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{child.age}</p>
              )}
            </FormField>
          </div>

          {/* Remove Button */}
          {formMode && (
            <button
              type="button"
              onClick={() => remove(index)}
              className="teal-button"
            >
              Remove
            </button>
          )}
        </div>
      ))}

      {formMode && (
        <button
          type="button"
          onClick={() =>
            append({
              firstName: '',
              middleName: '',
              lastName: '',
              birthDate: '',
              age: 0,
            })
          }
          className="teal-button"
        >
          Add Dependent
        </button>
      )}
    </SectionCard>
  );
}
