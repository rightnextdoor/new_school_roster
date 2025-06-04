/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/profile/sections/PhotoSection.tsx
import React, { useState, useEffect, ChangeEvent } from 'react';
import { useController, Control } from 'react-hook-form';
import SectionCard from '../SectionCard';
import PhotoCropper from '../PhotoCropper';

interface Props {
  control: Control<any>;
  formMode: boolean;
}

export default function PhotoSection({ control, formMode }: Props) {
  const fieldName = 'profilePicture';

  // Include a "required" rule here:
  const {
    field,
    fieldState, // gives us access to errors
  } = useController({
    name: fieldName as any,
    control,
    rules: { required: 'Photo is required' },
  });

  // Raw image data and preview
  const [rawPhoto, setRawPhoto] = useState<string>(field.value || '');
  const [previewPhoto, setPreviewPhoto] = useState<string>(field.value || '');
  const [isCropping, setIsCropping] = useState(false);

  // Whenever the form value changes (e.g. reset), keep preview in sync
  useEffect(() => {
    setPreviewPhoto(field.value || '');
  }, [field.value]);

  // When a file is chosen, immediately read it and enter crop mode
  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setRawPhoto(reader.result as string);
      setIsCropping(true);
    };
    reader.readAsDataURL(file);
  };

  // When cropping is done, write back into the form and exit crop mode
  const handleCropComplete = (cropped: string) => {
    field.onChange(cropped);
    setPreviewPhoto(cropped);
    setIsCropping(false);
  };

  // If in cropping mode, show the PhotoCropper full-screen
  if (isCropping) {
    return (
      <PhotoCropper
        src={rawPhoto}
        onComplete={handleCropComplete}
        onCancel={() => setIsCropping(false)}
      />
    );
  }

  // Normal photo upload / preview UI
  return (
    <SectionCard id="photo" title="Photo">
      {previewPhoto ? (
        <img
          src={previewPhoto}
          alt="avatar"
          className="avatar w-32 h-32 rounded-full object-cover"
        />
      ) : (
        <div className="w-32 h-32 bg-gray-200 rounded-full flex items-center justify-center">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-8 w-8 text-gray-400"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 4v16m8-8H4"
            />
          </svg>
        </div>
      )}

      {formMode && (
        <div className="mt-4">
          <label className="teal-button">
            Upload
            <input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="hidden"
            />
          </label>
        </div>
      )}

      {/* validation error message */}
      {fieldState.error && (
        <p className="text-red-600 mt-2">{fieldState.error.message}</p>
      )}
    </SectionCard>
  );
}
