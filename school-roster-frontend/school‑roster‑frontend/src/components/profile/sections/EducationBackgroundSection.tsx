/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/EducationBackgroundSection.tsx

import React from 'react';
import {
  useFieldArray,
  useFormContext,
  Controller,
  UseFormRegister,
  Control,
  FieldErrors,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import GooglePlacesLoader from '../../google/GooglePlacesLoader';
import AddressAutocomplete from '../AddressAutocomplete';
import type { NonStudentProfile, Address } from '../types';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  control: Control<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  formMode: boolean;
}

export default function EducationBackgroundSection({
  register,
  control,
  errors,
  formMode,
}: Props) {
  const { getValues } = useFormContext<NonStudentProfile>();

  const { fields, append, remove } = useFieldArray<
    NonStudentProfile,
    'educationalBackground'
  >({
    control,
    name: 'educationalBackground',
  });

  const today = new Date().toISOString().split('T')[0];

  return (
    <SectionCard id="education" title="Educational Background">
      <GooglePlacesLoader>
        {fields.map((field, index) => {
          const base = `educationalBackground.${index}` as const;
          const current = getValues(base) as any;

          return (
            <div key={field.id} className="mb-6">
              {/* Row 1: School Name, Level & Year Graduated */}
              <div className="grid grid-cols-4 gap-4">
                <FormField
                  label="School Name"
                  error={
                    errors.educationalBackground?.[index]?.schoolName
                      ?.message as string
                  }
                  formMode={formMode}
                >
                  {formMode ? (
                    <Controller
                      name={`${base}.schoolName`}
                      control={control}
                      rules={{ required: 'School name is required' }}
                      render={({ field: f }) => (
                        <input
                          {...f}
                          type="text"
                          className="block w-full border rounded p-2"
                        />
                      )}
                    />
                  ) : (
                    <p>{current.schoolName}</p>
                  )}
                </FormField>

                <FormField
                  label="Education Level"
                  error={
                    errors.educationalBackground?.[index]?.educationLevel
                      ?.message as string
                  }
                  formMode={formMode}
                >
                  {formMode ? (
                    <Controller
                      name={`${base}.educationLevel`}
                      control={control}
                      rules={{ required: 'Education level is required' }}
                      render={({ field: f }) => (
                        <input
                          {...f}
                          type="text"
                          className="block w-full border rounded p-2"
                        />
                      )}
                    />
                  ) : (
                    <p>{current.educationLevel}</p>
                  )}
                </FormField>

                <FormField
                  label="Year Graduated"
                  error={
                    errors.educationalBackground?.[index]?.yearGraduated
                      ?.message as string
                  }
                  formMode={formMode}
                >
                  {formMode ? (
                    <Controller
                      name={`${base}.yearGraduated` as const}
                      control={control}
                      rules={{ required: 'Year graduated is required' }}
                      render={({ field: f }) => (
                        <input
                          {...f}
                          type="number"
                          min={1900}
                          max={new Date().getFullYear()}
                          className="block w-full border rounded p-2"
                          onChange={(e) =>
                            f.onChange(
                              e.target.value === ''
                                ? undefined
                                : Number(e.target.value)
                            )
                          }
                          value={f.value ?? ''}
                        />
                      )}
                    />
                  ) : (
                    <p>{current.yearGraduated}</p>
                  )}
                </FormField>
              </div>

              {/* Header: School Address */}
              <h4 className="mt-6 mb-2 text-lg font-semibold">
                School Address
              </h4>
              {errors.educationalBackground?.[index]?.schoolAddress && (
                <p className="text-red-500 text-sm mb-2">
                  {errors.educationalBackground[index]!.schoolAddress!.message}
                </p>
              )}
              <Controller
                name={`${base}.schoolAddress` as any}
                control={control}
                rules={{
                  validate: (v: Address) =>
                    !!v?.streetAddress?.trim() || 'School address is required',
                }}
                render={({ field: f }) => {
                  const value = (f.value as Address) || {
                    streetAddress: '',
                    subdivision: '',
                    cityMunicipality: '',
                    provinceState: '',
                    country: '',
                    zipCode: '',
                  };
                  return (
                    <div className="space-y-4">
                      {/* Address Line 1 & 2 */}
                      <div className="grid grid-cols-3 gap-4">
                        <FormField
                          label="Address Line 1"
                          error={
                            errors.educationalBackground?.[index]?.schoolAddress
                              ?.streetAddress?.message as string
                          }
                          formMode={formMode}
                        >
                          {formMode ? (
                            <AddressAutocomplete
                              value={value}
                              onChange={f.onChange}
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
                                f.onChange({
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
                                f.onChange({
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
                                f.onChange({
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
                                f.onChange({
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
                                f.onChange({
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

              {/* Remove Button */}
              {formMode && (
                <button
                  type="button"
                  onClick={() => remove(index)}
                  className="teal-button"
                >
                  Remove Education
                </button>
              )}
            </div>
          );
        })}
      </GooglePlacesLoader>

      {/* Add Education */}
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
              educationLevel: '',
              yearGraduated: new Date().getFullYear(),
            })
          }
          className="teal-button"
        >
          Add Education
        </button>
      )}
    </SectionCard>
  );
}
