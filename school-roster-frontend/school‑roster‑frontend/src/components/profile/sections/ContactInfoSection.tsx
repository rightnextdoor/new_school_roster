/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/ContactInfoSection.tsx

import React from 'react';
import {
  Controller,
  useFieldArray,
  useFormContext,
  Control,
} from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import GooglePlacesLoader from '../../google/GooglePlacesLoader';
import AddressAutocomplete from '../AddressAutocomplete';
import PhoneInput, { isValidPhoneNumber } from 'react-phone-number-input';
import 'react-phone-number-input/style.css';
import type { Address } from '../types';
import { PhoneType } from '../enums';

interface Props {
  control: Control<any>;
  errors: any;
  formMode: boolean;
}

export default function ContactInfoSection({
  control,
  errors,
  formMode,
}: Props) {
  const { getValues } = useFormContext<any>();
  const {
    fields: phones,
    append,
    remove,
  } = useFieldArray<any, 'phoneNumbers'>({ control, name: 'phoneNumbers' });

  return (
    <SectionCard id="contact-info" title="Contact Info">
      {/* Current Address */}
      <h4 className="mt-2 mb-2 text-lg font-semibold">Current Address</h4>
      {errors.address && (
        <p className="text-red-500 text-sm mb-2">
          {errors.address.message as string}
        </p>
      )}
      <GooglePlacesLoader>
        <Controller
          name="address"
          control={control}
          rules={{
            validate: (v: Address) =>
              !!v?.streetAddress?.trim() || 'Current address is required',
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
                {/* Row 1: Street + Line 2 */}
                <div className="grid grid-cols-3 gap-4">
                  <FormField
                    label="Street Address"
                    error={errors.address?.streetAddress?.message}
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
                {/* Row 2: City + State */}
                <div className="grid grid-cols-3 gap-4">
                  <FormField label="City/Municipality" formMode={formMode}>
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
                {/* Row 3: ZIP + Country */}
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

      {/* Phone Numbers */}
      <h4 className="mt-6 mb-2 text-lg font-semibold">Phone Numbers</h4>
      {phones.map((p, idx) => (
        <div key={p.id} className="grid grid-cols-4 gap-4 mb-4">
          <FormField label="Type" formMode={formMode}>
            {formMode ? (
              <Controller
                name={`phoneNumbers.${idx}.type`}
                control={control}
                render={({ field }) => (
                  <select
                    {...field}
                    className="block w-full border rounded p-2"
                  >
                    {Object.values(PhoneType).map((t) => (
                      <option key={t} value={t}>
                        {t}
                      </option>
                    ))}
                  </select>
                )}
              />
            ) : (
              <p>{getValues(`phoneNumbers.${idx}.type`)}</p>
            )}
          </FormField>
          <FormField
            label="Number"
            error={errors.phoneNumbers?.[idx]?.number?.message}
            formMode={formMode}
          >
            {formMode ? (
              <Controller
                name={`phoneNumbers.${idx}.number`}
                control={control}
                rules={{
                  validate: (v) =>
                    isValidPhoneNumber(v || '') || 'Invalid phone number',
                }}
                render={({ field, fieldState }) => (
                  <>
                    <PhoneInput
                      value={field.value}
                      onChange={(val) => field.onChange(val ?? '')}
                      defaultCountry="PH"
                      international
                      countryCallingCodeEditable={false}
                      limitMaxLength
                      className="w-full border rounded p-2"
                    />
                    {fieldState.error && (
                      <p className="text-red-500 text-xs mt-1">
                        {fieldState.error.message}
                      </p>
                    )}
                  </>
                )}
              />
            ) : (
              <p>{getValues(`phoneNumbers.${idx}.number`)}</p>
            )}
          </FormField>
          {formMode && phones.length > 1 && (
            <button
              type="button"
              onClick={() => remove(idx)}
              className="teal-button"
            >
              Remove Phone
            </button>
          )}
        </div>
      ))}
      {formMode && (
        <button
          type="button"
          onClick={() => append({ type: PhoneType.HOME, number: '' })}
          className="teal-button"
        >
          Add Phone
        </button>
      )}
    </SectionCard>
  );
}
