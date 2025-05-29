// src/components/profile/NonStudentProfileForm.tsx

import React from 'react';
import { useForm, FormProvider } from 'react-hook-form';
import type { NonStudentProfile } from './types';
import PhotoSection from './sections/PhotoSection';
import UserInfoSection from './sections/UserInfoSection';
import ContactInfoSection from './sections/ContactInfoSection';
import DeptContactSection from './sections/DeptContactSection';
import CivilStatusFamilySection from './sections/CivilStatusFamilySection';
import DependentChildrenSection from './sections/DependentChildrenSection';
import CareerAppointmentsSection from './sections/CareerAppointmentsSection';
import EducationBackgroundSection from './sections/EducationBackgroundSection';
import GovernmentIDsSection from './sections/GovernmentIDsSection';

interface Props {
  nonStudent: NonStudentProfile;
  mode: 'edit' | 'create';
  onSave: (data: NonStudentProfile) => void;
  onCancel: () => void;
  saving?: boolean;
  user: {
    id: string;
    email: string;
    roles: string[];
  };
  roles: string[];
}

export default function NonStudentProfileForm({
  nonStudent,
  mode,
  onSave,
  onCancel,
  saving = false,
  user,
  roles,
}: Props) {
  const methods = useForm<NonStudentProfile>({
    mode: 'onChange',
    defaultValues: nonStudent,
  });
  const {
    handleSubmit,
    formState: { isValid, isSubmitted },
  } = methods;

  // formMode is always true for edit/create
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

        {/* Sections */}
        <PhotoSection control={methods.control} formMode={formMode} />

        <UserInfoSection
          register={methods.register}
          control={methods.control}
          user={user}
          roles={roles}
          errors={methods.formState.errors}
          isStudent={false}
          formMode={formMode}
        />

        <ContactInfoSection
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <DeptContactSection
          register={methods.register}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <CivilStatusFamilySection
          register={methods.register}
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <DependentChildrenSection
          register={methods.register}
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <CareerAppointmentsSection
          register={methods.register}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <EducationBackgroundSection
          register={methods.register}
          control={methods.control}
          errors={methods.formState.errors}
          formMode={formMode}
        />

        <GovernmentIDsSection
          register={methods.register}
          errors={methods.formState.errors}
          formMode={formMode}
        />
      </form>
    </FormProvider>
  );
}
