/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/CareerAppointmentsSection.tsx

import React from 'react';
import {
  useFieldArray,
  UseFormRegister,
  FieldErrors,
  useFormContext,
  useWatch,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import type { NonStudentProfile } from '../types';
import { AppointmentType, Position, SalaryGrade } from '../enums';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  formMode: boolean;
}

export default function CareerAppointmentsSection({
  register,
  errors,
  formMode,
}: Props) {
  const { control, setValue } = useFormContext<NonStudentProfile>();
  // watch the array
  const { fields, append, remove } = useFieldArray<NonStudentProfile>({
    control,
    name: 'employmentAppointments',
  });
  const appointments =
    useWatch({ control, name: 'employmentAppointments' }) || [];
  const today = new Date().toISOString().split('T')[0];

  return (
    <SectionCard id="career" title="Career & Appointments">
      {fields.map((field, idx) => {
        const app = appointments[idx] || ({} as any);
        const typeReg = register(
          `employmentAppointments.${idx}.appointmentType` as const,
          { required: 'Type is required' }
        );
        const posReg = register(
          `employmentAppointments.${idx}.position` as const,
          { required: 'Position is required' }
        );
        const dateReg = register(
          `employmentAppointments.${idx}.dateIssued` as const,
          { required: 'Date is required' }
        );

        // salary fields are gray-out
        const salaryGradeValue = app.salaryGrade;
        const salaryAmountReg = register(
          `employmentAppointments.${idx}.salaryAmount` as const,
          {
            required: 'Salary amount is required',
            min: { value: 0, message: 'Cannot be negative' },
          }
        );

        return (
          <div key={field.id} className="entry mb-4">
            {/* Row 1: Type & Position */}
            <div className="grid grid-cols-4 gap-4">
              <FormField
                label="Appointment Type"
                error={
                  errors.employmentAppointments?.[idx]?.appointmentType
                    ?.message as string
                }
                formMode={formMode}
              >
                {formMode ? (
                  <select
                    {...typeReg}
                    className="block w-full border rounded p-2"
                  >
                    {Object.values(AppointmentType).map((type) => (
                      <option key={type} value={type}>
                        {type}
                      </option>
                    ))}
                  </select>
                ) : (
                  <p>{app.appointmentType}</p>
                )}
              </FormField>

              <FormField
                label="Position"
                error={
                  errors.employmentAppointments?.[idx]?.position
                    ?.message as string
                }
                formMode={formMode}
              >
                {formMode ? (
                  <select
                    {...posReg}
                    className="block w-full border rounded p-2"
                    onChange={(e) => {
                      posReg.onChange(e);
                      const pos = e.target.value as Position;
                      const grade = SalaryGrade[pos];
                      setValue(
                        `employmentAppointments.${idx}.salaryGrade` as any,
                        grade
                      );
                    }}
                  >
                    {Object.values(Position).map((p) => (
                      <option key={p} value={p}>
                        {p}
                      </option>
                    ))}
                  </select>
                ) : (
                  <p>{app.position}</p>
                )}
              </FormField>
            </div>

            {/* Row 2: Salary Grade & Amount */}
            <div className="grid grid-cols-4 gap-4 mt-4">
              <FormField label="Salary Grade" formMode={formMode}>
                {formMode ? (
                  <input
                    type="text"
                    value={salaryGradeValue}
                    disabled
                    className="block w-full border rounded p-2 bg-gray-100 text-gray-500 cursor-not-allowed"
                  />
                ) : (
                  <p>{salaryGradeValue}</p>
                )}
              </FormField>

              <FormField
                label="Salary Amount"
                error={
                  errors.employmentAppointments?.[idx]?.salaryAmount
                    ?.message as string
                }
                formMode={formMode}
              >
                {formMode ? (
                  <input
                    type="number"
                    {...salaryAmountReg}
                    disabled
                    className="block w-full border rounded p-2 bg-gray-100 text-gray-500 cursor-not-allowed"
                  />
                ) : (
                  <p>{app.salaryAmount}</p>
                )}
              </FormField>
            </div>

            {/* Row 3: Date Issued */}
            <FormField
              label="Date Issued"
              error={
                errors.employmentAppointments?.[idx]?.dateIssued
                  ?.message as string
              }
              formMode={formMode}
            >
              {formMode ? (
                <input
                  type="date"
                  max={today}
                  {...dateReg}
                  className="block w-full border rounded p-2"
                />
              ) : (
                <p>{app.dateIssued}</p>
              )}
            </FormField>

            {/* Remove button */}
            {formMode && (
              <button
                type="button"
                onClick={() => remove(idx)}
                className="teal-button"
              >
                Remove
              </button>
            )}
          </div>
        );
      })}

      {/* Add Appointment */}
      {formMode && (
        <button
          type="button"
          onClick={() =>
            append({
              appointmentType: AppointmentType.ORIGINAL_APPOINTMENT,
              position: Position.TEACHER_I,
              salaryGrade: SalaryGrade[Position.TEACHER_I],
              salaryAmount: 0,
              dateIssued: '',
            })
          }
          className="teal-button"
        >
          Add Appointment
        </button>
      )}
    </SectionCard>
  );
}
