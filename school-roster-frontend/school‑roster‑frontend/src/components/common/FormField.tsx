// src/components/common/FormField.tsx

import React from 'react';

interface FormFieldProps {
  /** The label text shown above the field */
  label: string;
  /** Optional error message shown below the field */
  error?: string;
  /** Whether the form is in edit mode; can be used for conditional styling */
  formMode: boolean;
  /** The input or display element(s) for this field */
  children: React.ReactNode;
}

export default function FormField({ label, error, children }: FormFieldProps) {
  return (
    <div className="mb-4">
      <label className="block text-sm font-semibold mb-1">{label}</label>
      {/* Wrap all fields in a max-width container */}
      <div className="w-full max-w-sm border-2 border-gray-800 p-1">
        {children}
      </div>
      {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
    </div>
  );
}
