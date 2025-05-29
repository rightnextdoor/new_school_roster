/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/SchoolHistorySection.tsx

import React from 'react';
import {
  useFieldArray,
  useFormContext,
  Controller,
  UseFormRegister,
  FieldErrors,
  Control,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import GooglePlacesLoader from '../../google/GooglePlacesLoader';
import AddressAutocomplete from '../AddressAutocomplete';
import type { StudentProfile, Address } from '../types';
import { StudentGradeStatus } from '../enums';

interface Props {
  register: UseFormRegister<StudentProfile>;
  control: Control<StudentProfile>;
  errors: FieldErrors<StudentProfile>;
  formMode: boolean;
}

export default function SchoolHistorySection({
  register,
  control,
  errors,
  formMode,
}: Props) {
  const { getValues, setValue } = useFormContext<StudentProfile>();
  const { fields, append, remove } = useFieldArray<
    StudentProfile,
    'schoolHistories'
  >({
    control,
    name: 'schoolHistories',
  });
  const today = new Date().toISOString().split('T')[0];

  const calculateGradeStatus = (gpa: number | null): StudentGradeStatus => {
    if (gpa == null) return StudentGradeStatus.FAILED;
    if (gpa >= 98) return StudentGradeStatus.WITH_HIGHEST_HONORS;
    if (gpa >= 95) return StudentGradeStatus.WITH_HIGH_HONORS;
    if (gpa >= 90) return StudentGradeStatus.WITH_HONORS;
    if (gpa >= 75) return StudentGradeStatus.PASSED;
    return StudentGradeStatus.FAILED;
  };

  return (
    <GooglePlacesLoader>
      <SectionCard id="history" title="School History">
        {fields.map((item, index) => {
          const base = `schoolHistories.${index}` as const;
          const current = getValues(base) as any;
          const errGroup = errors.schoolHistories?.[index] || {};

          return (
            <div key={item.id} className="mb-6">
              {/* Row 1: School Name, Grade Level, Section Nicknames */}
              <div className="grid grid-cols-3 gap-4">
                <FormField
                  label="School Name"
                  error={errGroup.schoolName?.message}
                  formMode={formMode}
                >
                  {formMode ? (
                    <input
                      type="text"
                      {...register(`${base}.schoolName`, {
                        required: 'School name is required',
                      })}
                      className="block w-full border rounded p-2"
                    />
                  ) : (
                    <p>{current.schoolName}</p>
                  )}
                </FormField>

                <FormField
                  label="Grade Level"
                  error={
                    errors.schoolHistories?.[index]?.gradeLevel
                      ?.message as string
                  }
                  formMode={formMode}
                >
                  {formMode ? (
                    <Controller
                      name={`${base}.gradeLevel` as const}
                      control={control}
                      rules={{
                        required: 'Grade level is required',
                        validate: (v: any) =>
                          (typeof v === 'string' && v.trim().length > 0) ||
                          'Grade level cannot be blank',
                      }}
                      render={({ field }) => (
                        <input
                          {...field}
                          type="text"
                          className="block w-full border rounded p-2"
                          placeholder="e.g. K, 1, 2A"
                        />
                      )}
                    />
                  ) : (
                    <p>{current.gradeLevel}</p>
                  )}
                </FormField>

                <FormField
                  label="Section Nickname"
                  error={
                    errors.schoolHistories?.[index]?.sectionNicknames
                      ?.message as string
                  }
                  formMode={formMode}
                >
                  {formMode ? (
                    <input
                      type="text"
                      {...register(`${base}.sectionNicknames`, {
                        required: 'Section nickname is required',
                      })}
                      className="block w-full border rounded p-2"
                      placeholder="e.g. A, B, C"
                    />
                  ) : (
                    <p>{current.sectionNicknames}</p>
                  )}
                </FormField>
              </div>

              {/* Row 2: Start Year, End Year */}
              <div className="grid grid-cols-2 gap-4 mt-4">
                <FormField
                  label="Start Year"
                  error={errGroup.schoolYearStart?.message}
                  formMode={formMode}
                >
                  {formMode ? (
                    <input
                      type="date"
                      {...register(`${base}.schoolYearStart`, {
                        required: 'Start year is required',
                      })}
                      max={today}
                      className="block w-full border rounded p-2"
                    />
                  ) : (
                    <p>{current.schoolYearStart}</p>
                  )}
                </FormField>

                <FormField
                  label="End Year"
                  error={errGroup.schoolYearEnd?.message}
                  formMode={formMode}
                >
                  {formMode ? (
                    <input
                      type="date"
                      {...register(`${base}.schoolYearEnd`, {
                        required: 'End year is required',
                      })}
                      max={today}
                      className="block w-full border rounded p-2"
                    />
                  ) : (
                    <p>{current.schoolYearEnd}</p>
                  )}
                </FormField>
              </div>

              {/* Row 3: Completed, GPA, Status */}
              <div className="grid grid-cols-3 gap-4 mt-4">
                <FormField label="Completed" formMode={formMode}>
                  {formMode ? (
                    <input type="checkbox" {...register(`${base}.completed`)} />
                  ) : (
                    <p>{current.completed ? 'Yes' : 'No'}</p>
                  )}
                </FormField>

                <FormField
                  label="GPA"
                  error={errGroup.gpa?.message}
                  formMode={formMode}
                >
                  {formMode ? (
                    <input
                      type="number"
                      step="0.01"
                      {...register(`${base}.gpa`, {
                        valueAsNumber: true,
                        onChange: (e) => {
                          const gpaVal = parseFloat(e.target.value);
                          setValue(
                            `${base}.gradeStatus`,
                            calculateGradeStatus(isNaN(gpaVal) ? null : gpaVal)
                          );
                        },
                      })}
                      className="block w-full border rounded p-2"
                    />
                  ) : (
                    <p>{current.gpa.toFixed(2)}</p>
                  )}
                </FormField>

                <FormField label="Status" formMode={formMode}>
                  {formMode ? (
                    <Controller
                      name={`${base}.gradeStatus` as const}
                      control={control}
                      render={({ field }) => (
                        <input
                          {...field}
                          type="text"
                          disabled
                          className="block w-full bg-gray-100 border rounded p-2 text-gray-500 cursor-not-allowed"
                        />
                      )}
                    />
                  ) : (
                    // when viewing, read it directly from form values
                    <p className="text-gray-500">
                      {getValues(`${base}.gradeStatus`)}
                    </p>
                  )}
                </FormField>
              </div>

              {/* Header: School Address */}
              <h4 className="mt-6 mb-2 text-lg font-semibold">
                School Address
              </h4>
              {errors.schoolHistories?.[index]?.schoolAddress?.message && (
                <p className="text-red-500 text-sm mb-2">
                  {
                    errors.schoolHistories[index].schoolAddress
                      .message as string
                  }
                </p>
              )}
              <Controller
                name={`${base}.schoolAddress`}
                control={control}
                rules={{
                  // require a non‐blank streetAddress
                  validate: (v: Address) =>
                    !!v?.streetAddress?.trim() || 'School address is required',
                }}
                render={({ field }) => {
                  const value = (field.value as Address) || {
                    streetAddress: '',
                    subdivision: '',
                    cityMunicipality: '',
                    provinceState: '',
                    country: '',
                    zipCode: '',
                  };
                  return (
                    <div className="space-y-4">
                      {/* Line 1 & 2 */}
                      <div className="grid grid-cols-3 gap-4">
                        <FormField
                          label="Address Line 1"
                          error={errGroup.schoolAddress?.streetAddress?.message}
                          formMode={formMode}
                        >
                          {formMode ? (
                            <AddressAutocomplete
                              value={value}
                              onChange={field.onChange}
                            />
                          ) : (
                            <p>{value.streetAddress}</p>
                          )}
                        </FormField>

                        <FormField label="Address Line 2" formMode={formMode}>
                          {formMode ? (
                            <input
                              type="text"
                              value={value.subdivision}
                              onChange={(e) =>
                                field.onChange({
                                  ...value,
                                  subdivision: e.target.value,
                                })
                              }
                              className="block w-full border rounded p-2"
                            />
                          ) : (
                            <p>{value.subdivision}</p>
                          )}
                        </FormField>
                      </div>

                      {/* City & State */}
                      <div className="grid grid-cols-3 gap-4">
                        <FormField
                          label="City/Municipality"
                          formMode={formMode}
                        >
                          {formMode ? (
                            <input
                              type="text"
                              value={value.cityMunicipality}
                              onChange={(e) =>
                                field.onChange({
                                  ...value,
                                  cityMunicipality: e.target.value,
                                })
                              }
                              className="block w-full border rounded p-2"
                            />
                          ) : (
                            <p>{value.cityMunicipality}</p>
                          )}
                        </FormField>

                        <FormField
                          label="State/Province/Region"
                          formMode={formMode}
                        >
                          {formMode ? (
                            <input
                              type="text"
                              value={value.provinceState}
                              onChange={(e) =>
                                field.onChange({
                                  ...value,
                                  provinceState: e.target.value,
                                })
                              }
                              className="block w-full border rounded p-2"
                            />
                          ) : (
                            <p>{value.provinceState}</p>
                          )}
                        </FormField>
                      </div>

                      {/* ZIP & Country */}
                      <div className="grid grid-cols-3 gap-4">
                        <FormField label="Postal/ZIP Code" formMode={formMode}>
                          {formMode ? (
                            <input
                              type="text"
                              value={value.zipCode}
                              onChange={(e) =>
                                field.onChange({
                                  ...value,
                                  zipCode: e.target.value,
                                })
                              }
                              className="block w-full border rounded p-2"
                            />
                          ) : (
                            <p>{value.zipCode}</p>
                          )}
                        </FormField>

                        <FormField label="Country" formMode={formMode}>
                          {formMode ? (
                            <input
                              type="text"
                              value={value.country}
                              onChange={(e) =>
                                field.onChange({
                                  ...value,
                                  country: e.target.value,
                                })
                              }
                              className="block w-full border rounded p-2"
                            />
                          ) : (
                            <p>{value.country}</p>
                          )}
                        </FormField>
                      </div>
                    </div>
                  );
                }}
              />

              {formMode && (
                <button
                  type="button"
                  onClick={() => remove(index)}
                  className="teal-button mt-4"
                >
                  Remove History
                </button>
              )}
            </div>
          );
        })}

        {formMode && (
          <button
            type="button"
            onClick={() =>
              append({
                schoolName: '',
                schoolAddress: {
                  streetAddress: '',
                  subdivision: '',
                  cityMunicipality: '',
                  provinceState: '',
                  country: '',
                  zipCode: '',
                },
                gradeLevel: '',
                sectionNicknames: '',
                schoolYearStart: '',
                schoolYearEnd: '',
                completed: false,
                gpa: 0,
                gradeStatus: StudentGradeStatus.FAILED,
              })
            }
            className="teal-button"
          >
            Add History
          </button>
        )}
      </SectionCard>
    </GooglePlacesLoader>
  );
}
