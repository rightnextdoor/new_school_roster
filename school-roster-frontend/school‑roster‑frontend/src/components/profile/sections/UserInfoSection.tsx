/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/UserInfoSection.tsx

import React from 'react';
import {
  useController,
  useFormContext,
  Controller,
  useFieldArray,
  UseFormRegister,
  Control,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import GooglePlacesLoader from '../../google/GooglePlacesLoader';
import AddressAutocomplete from '../AddressAutocomplete';
import type { Address } from '../types';
import { Role } from '../enums';

interface Props {
  register: UseFormRegister<any>;
  control: Control<any>;
  errors: any;
  isStudent: boolean;
  formMode: boolean;
  user: {
    id: string;
    email: string;
    roles: string[];
  };
  roles: string[];
}

export default function UserInfoSection({
  register,
  control,
  errors,
  isStudent,
  formMode,
  user,
  roles,
}: Props) {
  const { getValues } = useFormContext<any>();
  const { field: idField } = useController({ name: 'id' as const, control });
  const today = new Date().toISOString().split('T')[0];

  // Watch role to conditionally show Grade Level
  const roleValue = roles;

  // Dynamic arrays for religion and dialects
  const {
    fields: dialectFields,
    append: appendDialect,
    remove: removeDialect,
  } = useFieldArray<any, 'dialects'>({ control, name: 'dialects' });

  return (
    <SectionCard id="user-info" title="User Info">
      {/* Row 1: ID */}
      <FormField label="User ID" formMode={formMode}>
        {/* keep the profile-id hidden so handleSave still sees it */}
        {formMode && <input type="hidden" {...register('id')} />}
        {/* but display the real user ID */}
        <p className="block w-full bg-gray-100 border rounded p-2 text-gray-500 cursor-not-allowed">
          {user?.id}
        </p>
      </FormField>

      {/* Row 2: Full Name */}
      <div className="grid grid-cols-4 gap-4">
        <FormField
          label="First Name"
          error={errors.firstName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('firstName', { required: 'First name is required' })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('firstName')}</p>
          )}
        </FormField>
        <FormField label="Middle Name" formMode={formMode}>
          {formMode ? (
            <input
              type="text"
              {...register('middleName')}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('middleName')}</p>
          )}
        </FormField>
        <FormField
          label="Last Name"
          error={errors.lastName?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('lastName', { required: 'Last name is required' })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('lastName')}</p>
          )}
        </FormField>
      </div>

      {/* Row 3: Gender */}
      <FormField
        label="Gender"
        error={errors.gender?.message}
        formMode={formMode}
      >
        {formMode ? (
          <select
            {...register('gender', { required: 'Gender is required' })}
            className="block w-full border rounded p-2"
          >
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        ) : (
          <p>{getValues('gender')}</p>
        )}
      </FormField>

      {/* Row 4: Grade Level */}
      {(roleValue.includes(Role.STUDENT) ||
        roleValue.includes(Role.TEACHER) ||
        roleValue.includes(Role.TEACHER_LEAD)) && (
        <FormField
          label="Grade Level"
          error={errors.gradeLevel?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="number"
              {...register('gradeLevel', {
                required: 'Grade level is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('gradeLevel')}</p>
          )}
        </FormField>
      )}

      {/* Row 5: Email & Date of Birth */}
      <div className="grid grid-cols-3 gap-4">
        <FormField label="Email" formMode={formMode}>
          {/* keep the original email hidden if your API expects it */}
          {formMode && <input type="hidden" {...register('email')} />}
          {/* but display the real user email */}
          <p className="block w-full bg-gray-100 border rounded p-2 text-gray-500 cursor-not-allowed">
            {user?.email}
          </p>
        </FormField>
        <FormField
          label="Date of Birth"
          error={errors.birthDate?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="date"
              max={today}
              {...register('birthDate', {
                required: 'Date of birth is required',
              })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('birthDate')}</p>
          )}
        </FormField>
      </div>

      {/* Row 5: Religions */}
      {isStudent && (
        <FormField
          label="Religion"
          error={errors.religion?.message}
          formMode={formMode}
        >
          {formMode ? (
            <input
              type="text"
              {...register('religion', { required: 'Religion is required' })}
              className="block w-full border rounded p-2"
            />
          ) : (
            <p>{getValues('religion')}</p>
          )}
        </FormField>
      )}

      {/* Row 6: Dialects */}
      {isStudent && (
        <>
          <h4 className="mt-6 mb-2 text-lg font-semibold">Dialects</h4>
          {dialectFields.map((field, idx) => (
            <div key={field.id} className="grid grid-cols-4 gap-4 mb-4">
              <FormField
                label={idx === 0 ? 'Dialect' : ''}
                error={errors.dialects?.[idx]?.message}
                formMode={formMode}
              >
                {formMode ? (
                  <input
                    type="text"
                    {...register(`dialects.${idx}` as const, {
                      required: 'Dialect is required',
                    })}
                    className="block w-full border rounded p-2"
                  />
                ) : (
                  <p>{getValues(`dialects.${idx}`)}</p>
                )}
              </FormField>
              {formMode && dialectFields.length > 1 && (
                <button
                  type="button"
                  onClick={() => removeDialect(idx)}
                  className="teal-button"
                >
                  Remove Dialect
                </button>
              )}
            </div>
          ))}
          {formMode && (
            <button
              type="button"
              onClick={() => appendDialect('')}
              className="teal-button"
            >
              Add Dialect
            </button>
          )}
        </>
      )}

      {/* Row 7: Birthplace Address */}
      <h4 className="mt-6 mb-2 text-lg font-semibold">Birthplace Address</h4>
      {errors.birthPlace && (
        <p className="text-red-500 text-sm mb-2">
          {errors.birthPlace.message as string}
        </p>
      )}
      <GooglePlacesLoader>
        <Controller
          name="birthPlace"
          control={control}
          rules={{
            validate: (v: Address) =>
              !!v?.streetAddress?.trim() || 'Birthplace is required',
          }}
          render={({ field }) => {
            const value: Address = field.value || {
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
                    error={errors.birthPlace?.streetAddress?.message}
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

                {/* City, State/Province/Region */}
                <div className="grid grid-cols-3 gap-4">
                  <FormField label="City" formMode={formMode}>
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

                  <FormField label="State/Province/Region" formMode={formMode}>
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
                          field.onChange({ ...value, zipCode: e.target.value })
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
                          field.onChange({ ...value, country: e.target.value })
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
      </GooglePlacesLoader>
    </SectionCard>
  );
}
