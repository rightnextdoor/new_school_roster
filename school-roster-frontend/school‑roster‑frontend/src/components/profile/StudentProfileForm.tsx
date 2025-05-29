// src/components/profile/StudentProfileForm.tsx

import React from 'react';
import { useForm, FormProvider } from 'react-hook-form';
import type { StudentProfile } from './types';
import PhotoSection from './sections/PhotoSection';
import UserInfoSection from './sections/UserInfoSection';
import ContactInfoSection from './sections/ContactInfoSection';
import ParentFamilySection from './sections/ParentFamilySection';
import NutritionalSection from './sections/NutritionalSection';
import AcademicSection from './sections/AcademicSection';
import SchoolHistorySection from './sections/SchoolHistorySection';

interface Props {
  student: StudentProfile;
  mode: 'edit' | 'create';
  onSave: (data: StudentProfile) => void;
  onCancel: () => void;
  saving?: boolean;
  user: {
    id: string;
    email: string;
    roles: string[];
  };
  roles: string[];
}

export default function StudentProfileForm({
  student,
  mode,
  onSave,
  onCancel,
  saving = false,
  user,
  roles,
}: Props) {
  const methods = useForm<StudentProfile>({
    mode: 'onChange',
    defaultValues: student,
  });

  const {
    handleSubmit,
    formState: { isValid, isSubmitted },
  } = methods;

  // formMode enabled for both edit and create
  const formMode = mode === 'edit' || mode === 'create';

  return (
    <FormProvider {...methods}>
      <form onSubmit={handleSubmit(onSave)} autoComplete="off">
        {/* Top-of-form validation warning */}
        {isSubmitted && !isValid && (
          <div className="bg-red-100 border-l-4 border-red-500 text-red-700 p-3 mb-4">
            Please fill in all required fields before saving.
          </div>
        )}

        {/* Toolbar */}
        {formMode && (
          <div className="flex justify-end space-x-2 mb-6">
            <button
              type="button"
              onClick={onCancel}
              className="teal-button"
              disabled={saving}
            >
              Cancel
            </button>
            <button type="submit" className="teal-button" disabled={saving}>
              {saving ? (
                <div
                  className="spinner"
                  style={{ width: '20px', height: '20px', borderWidth: '3px' }}
                />
              ) : mode === 'edit' ? (
                'Save Changes'
              ) : (
                'Create Profile'
              )}
            </button>
          </div>
        )}

        {/* Profile sections */}
        <PhotoSection control={methods.control} formMode={formMode} />

        <UserInfoSection
          register={methods.register}
          control={methods.control}
          user={user}
          roles={roles}
          errors={methods.formState.errors}
          isStudent={true}
          formMode={formMode}
        />

        <ContactInfoSection
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <ParentFamilySection
          register={methods.register}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <NutritionalSection
          register={methods.register}
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <AcademicSection
          register={methods.register}
          errors={methods.formState.errors}
          student={student}
          formMode={formMode}
        />

        <SchoolHistorySection
          register={methods.register}
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />
      </form>
    </FormProvider>
  );
}
