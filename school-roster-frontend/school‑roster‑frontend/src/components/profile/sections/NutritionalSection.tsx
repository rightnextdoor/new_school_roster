// src/components/profile/sections/NutritionalSection.tsx

import React from 'react';
import {
  UseFormRegister,
  FieldErrors,
  Control,
  useController,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { StudentProfile } from '../types';

interface Props {
  register: UseFormRegister<StudentProfile>;
  control: Control<StudentProfile>;
  errors: FieldErrors<StudentProfile>;
  formMode: boolean;
}

export default function NutritionalSection({
  register,
  control,
  errors,
  formMode,
}: Props) {
  // Controlled values for height, weight, BMI, and category
  const {
    field: { value: height },
  } = useController({ name: 'nutritionalStatus.heightInMeters', control });
  const {
    field: { value: weight },
  } = useController({ name: 'nutritionalStatus.weightInKilograms', control });
  const {
    field: { value: bmi },
  } = useController({ name: 'nutritionalStatus.bmi', control });
  const {
    field: { value: category },
  } = useController({ name: 'nutritionalStatus.bmiCategory', control });

  return (
    <SectionCard id="nutritional" title="Nutritional Status">
      {/* Row 1: Height & Weight */}
      <div className="grid grid-cols-2 gap-4">
        <FormField
          label="Height (m)"
          error={errors.nutritionalStatus?.heightInMeters?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="number"
              step="0.01"
              {...register('nutritionalStatus.heightInMeters', {
                required: 'Height is required',
                min: { value: 0.1, message: 'Enter a valid height' },
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{height}</p>
          )}
        </FormField>

        <FormField
          label="Weight (kg)"
          error={errors.nutritionalStatus?.weightInKilograms?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="number"
              step="0.1"
              {...register('nutritionalStatus.weightInKilograms', {
                required: 'Weight is required',
                min: { value: 0.1, message: 'Enter a valid weight' },
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{weight}</p>
          )}
        </FormField>
      </div>

      {/* Row X: BMI & Category */}
      <div className="grid grid-cols-2 gap-4">
        <FormField label="BMI" formMode={formMode}>
          {formMode ? (
            <input
              type="text"
              value={bmi}
              disabled
              className="block w-full bg-gray-100 border rounded p-2 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <p className="text-gray-500">{bmi}</p>
          )}
        </FormField>

        <FormField label="Category" formMode={formMode}>
          {formMode ? (
            <input
              type="text"
              value={category}
              disabled
              className="block w-full bg-gray-100 border rounded p-2 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <p className="text-gray-500">{category}</p>
          )}
        </FormField>
      </div>
    </SectionCard>
  );
}
