import React from 'react';
import { useFormContext, UseFormRegister, FieldErrors } from 'react-hook-form';
import SectionCard from '../SectionCard';
import FormField from '../../common/FormField';
import { maskLast4 } from '../../../utils/mask';
import type { NonStudentProfile } from '../types';

interface Props {
  register: UseFormRegister<NonStudentProfile>;
  errors: FieldErrors<NonStudentProfile>;
  formMode: boolean;
}

export default function GovernmentIDsSection({
  register,
  errors,
  formMode,
}: Props) {
  const { getValues } = useFormContext<NonStudentProfile>();

  // capture initial loaded values; only these determine disabling
  const initialTax = React.useRef(getValues('taxNumberEncrypted') || '');
  const initialGsis = React.useRef(getValues('gsisNumberEncrypted') || '');
  const initialPag = React.useRef(getValues('pagIbigNumberEncrypted') || '');
  const initialPhil = React.useRef(
    getValues('philHealthNumberEncrypted') || ''
  );

  // current raw values (decrypted by service)
  const taxVal = getValues('taxNumberEncrypted') || '';
  const gsisVal = getValues('gsisNumberEncrypted') || '';
  const pagIbigVal = getValues('pagIbigNumberEncrypted') || '';
  const philVal = getValues('philHealthNumberEncrypted') || '';

  return (
    <SectionCard id="gov-ids" title="Government IDs">
      {/* Tax Number */}
      <FormField
        label="Tax Number"
        error={errors.taxNumberEncrypted?.message}
        formMode={formMode}
      >
        {formMode ? (
          initialTax.current ? (
            <input
              type="text"
              disabled
              placeholder={maskLast4(initialTax.current)}
              className="block w-full p-2 rounded bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <input
              type="text"
              inputMode="numeric"
              pattern="\d*"
              maxLength={12}
              {...register('taxNumberEncrypted', {
                validate: (v) =>
                  !v ||
                  /^(?:\d{9}|\d{12})$/.test(v) ||
                  'Tax number must be 9 or 12 digits',
              })}
              className="block w-full p-2 rounded border"
            />
          )
        ) : (
          <p>{taxVal ? maskLast4(taxVal) : '—'}</p>
        )}
      </FormField>

      {/* GSIS Number */}
      <FormField
        label="GSIS Number"
        error={errors.gsisNumberEncrypted?.message}
        formMode={formMode}
      >
        {formMode ? (
          initialGsis.current ? (
            <input
              type="text"
              disabled
              placeholder={maskLast4(initialGsis.current)}
              className="block w-full p-2 rounded bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <input
              type="text"
              inputMode="numeric"
              pattern="\d*"
              maxLength={11}
              {...register('gsisNumberEncrypted', {
                validate: (v) =>
                  !v || /^\d{11}$/.test(v) || 'GSIS number must be 11 digits',
              })}
              className="block w-full p-2 rounded border"
            />
          )
        ) : (
          <p>{gsisVal ? maskLast4(gsisVal) : '—'}</p>
        )}
      </FormField>

      {/* Pag-IBIG Number */}
      <FormField
        label="Pag-IBIG Number"
        error={errors.pagIbigNumberEncrypted?.message}
        formMode={formMode}
      >
        {formMode ? (
          initialPag.current ? (
            <input
              type="text"
              disabled
              placeholder={maskLast4(initialPag.current)}
              className="block w-full p-2 rounded bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <input
              type="text"
              inputMode="numeric"
              pattern="\d*"
              maxLength={12}
              {...register('pagIbigNumberEncrypted', {
                validate: (v) =>
                  !v ||
                  /^\d{12}$/.test(v) ||
                  'Pag-IBIG number must be 12 digits',
              })}
              className="block w-full p-2 rounded border"
            />
          )
        ) : (
          <p>{pagIbigVal ? maskLast4(pagIbigVal) : '—'}</p>
        )}
      </FormField>

      {/* PhilHealth Number */}
      <FormField
        label="PhilHealth Number"
        error={errors.philHealthNumberEncrypted?.message}
        formMode={formMode}
      >
        {formMode ? (
          initialPhil.current ? (
            <input
              type="text"
              disabled
              placeholder={maskLast4(initialPhil.current)}
              className="block w-full p-2 rounded bg-gray-100 text-gray-500 cursor-not-allowed"
            />
          ) : (
            <input
              type="text"
              inputMode="numeric"
              pattern="\d*"
              maxLength={12}
              {...register('philHealthNumberEncrypted', {
                validate: (v) =>
                  !v ||
                  /^\d{12}$/.test(v) ||
                  'PhilHealth number must be 12 digits',
              })}
              className="block w-full p-2 rounded border"
            />
          )
        ) : (
          <p>{philVal ? maskLast4(philVal) : '—'}</p>
        )}
      </FormField>
    </SectionCard>
  );
}
