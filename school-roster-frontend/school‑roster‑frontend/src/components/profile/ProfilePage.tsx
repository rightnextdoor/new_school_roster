/* eslint-disable @typescript-eslint/no-unused-vars */
/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './profile.css';

import ProfileHeader from './ProfileHeader';
import ProfileForm from './ProfileForm';
import StudentProfile from './StudentProfile';
import NonStudentProfile from './NonStudentProfile';
import ProfileLoadingSections from './ProfileLoadingSections';
import ProfileNav from './ProfileNav';

import {
  getProfileByUserId,
  updateStudentProfile,
  updateNonStudentProfile,
  createStudentProfile,
  createNonStudentProfile,
} from '../../services/profileApi';
import { useAuth } from '../../contexts/AuthContext';
import { encryptField } from '../../utils/crypto';

interface ProfilePageProps {
  user: {
    id: string;
    email: string;
    roles: string[];
  };
  editMode?: boolean;
  backTo: string;
  onCloseModal?: () => void;
}

const ProfilePage: React.FC<ProfilePageProps> = ({
  user,
  editMode = false,
  backTo,
  onCloseModal,
}) => {
  const [profile, setProfile] = useState<any | null | undefined>(undefined);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  const navigate = useNavigate();
  const { user: loginUser } = useAuth();

  // Determine roles from user object
  const loginRoles = Array.isArray(loginUser?.roles) ? loginUser.roles : [];
  const loginStudent = loginRoles.includes('STUDENT');
  const roles = Array.isArray(user?.roles) ? user.roles : [];
  const profileStudent = roles.includes('STUDENT');

  const canEdit =
    loginRoles.includes('TEACHER') ||
    loginRoles.includes('TEACHER_LEAD') ||
    loginRoles.includes('ADMINISTRATOR') ||
    loginRoles.includes('ADMIN');

  // refs to track initial gov-id presence
  const initialTax = useRef<boolean>(false);
  const initialGsis = useRef<boolean>(false);
  const initialPhil = useRef<boolean>(false);
  const initialPag = useRef<boolean>(false);

  // 1) Load profile on mount
  useEffect(() => {
    if (!user) return;
    (async () => {
      try {
        const data = await getProfileByUserId(user.id);
        if (data === null) {
          // we simply set profile to null; modal is shown from DashboardPage instead
          setProfile(null);
        } else {
          // record which gov IDs exist
          if ('taxNumberEncrypted' in data) {
            initialTax.current = !!data.taxNumberEncrypted;
            initialGsis.current = !!data.gsisNumberEncrypted;
            initialPhil.current = !!data.philHealthNumberEncrypted;
            initialPag.current = !!data.pagIbigNumberEncrypted;
          }
          setProfile(data);
        }
      } catch (err: any) {
        console.error('Failed to load profile', err);
        setLoadError(
          'Failed to load profile. Please reload or go back to the dashboard.'
        );
      }
    })();
  }, [user]);

  // Save
  const handleSave = async (formData: any) => {
    if (!user) return;

    // 1) Clone & only encrypt any newly‐entered gov-IDs
    const payload: any = { ...formData };
    if (!initialTax.current && payload.taxNumberEncrypted) {
      payload.taxNumberEncrypted = encryptField(payload.taxNumberEncrypted);
    } else {
      delete payload.taxNumberEncrypted;
    }
    if (!initialGsis.current && payload.gsisNumberEncrypted) {
      payload.gsisNumberEncrypted = encryptField(payload.gsisNumberEncrypted);
    } else {
      delete payload.gsisNumberEncrypted;
    }
    if (!initialPhil.current && payload.philHealthNumberEncrypted) {
      payload.philHealthNumberEncrypted = encryptField(
        payload.philHealthNumberEncrypted
      );
    } else {
      delete payload.philHealthNumberEncrypted;
    }
    if (!initialPag.current && payload.pagIbigNumberEncrypted) {
      payload.pagIbigNumberEncrypted = encryptField(
        payload.pagIbigNumberEncrypted
      );
    } else {
      delete payload.pagIbigNumberEncrypted;
    }

    setSaveError(null);
    setSaving(true);

    try {
      let saved;
      if (profile) {
        // === UPDATE existing ===
        const updateDto = {
          ...payload,
          linkedUser: { id: user.id },
        };
        if (profileStudent) {
          saved = await updateStudentProfile(profile.id, updateDto);
        } else {
          saved = await updateNonStudentProfile(profile.id, updateDto);
        }
      } else {
        // === CREATE new ===
        // strip off the form-generated `id` and pull out role
        const { id: _discard, ...createData } = payload;

        if (profileStudent) {
          // embed linkedUser in the DTO
          const studentDto = {
            ...createData,
            linkedUser: { id: user.id },
          };
          saved = await createStudentProfile(user.id, studentDto);
        } else {
          const nonStudentDto = {
            ...createData,
            linkedUser: { id: user.id },
          };
          saved = await createNonStudentProfile(user.id, nonStudentDto);
        }
      }

      setProfile(saved);
      if (backTo === 'CLOSE_MODAL' && onCloseModal) {
        onCloseModal();
      } else {
        navigate(`/profile/${user.id}`, { state: { user, from: backTo } });
      }
    } catch (err: any) {
      console.error('Failed to save profile', err);
      const msg =
        err.response?.data?.message ||
        (typeof err.response?.data === 'string'
          ? err.response.data
          : 'Failed to save profile. Please try again.');
      setSaveError(msg);
      setTimeout(() => setSaveError(null), 5000);
    } finally {
      setSaving(false);
    }
  };

  // 2) Loading skeleton
  if (profile === undefined) {
    return (
      <div className="profile-container">
        <ProfileNav isStudent={profileStudent} />
        <main className="profile-main">
          {loadError && <div className="error-banner">{loadError}</div>}
          <ProfileLoadingSections isStudent={profileStudent} />
        </main>
      </div>
    );
  }

  // 3) Edit/Create form (allow create when profile===null)
  if (editMode) {
    const mode = profile ? 'edit' : 'create';
    const initialData =
      profile ||
      ({
        id: user?.id,
        email: user?.email,
        profilePicture: '',
      } as any);

    return (
      <div className="profile-container">
        <ProfileNav isStudent={profileStudent} />
        <main className="profile-main">
          {saveError && <div className="error-banner">{saveError}</div>}
          <ProfileForm
            data={initialData}
            mode={mode}
            user={user}
            onSave={handleSave}
            onCancel={() => {
              if (backTo === 'CLOSE_MODAL' && onCloseModal) {
                onCloseModal();
              } else {
                navigate(`/profile/${user.id}`, {
                  state: { user, from: backTo },
                });
              }
            }}
            saving={saving}
          />
        </main>
      </div>
    );
  }

  // 4) Read-only view (here, profile is guaranteed non-null)
  const fullName = [profile.firstName, profile.middleName, profile.lastName]
    .filter(Boolean)
    .join(' ');
  const avatarUrl = profile.profilePicture;
  const primaryRole = roles[0] || '';
  const roleName = primaryRole
    .split('_')
    .map((w) => w.charAt(0) + w.slice(1).toLowerCase())
    .join(' ');
  const gradePart = profile.gradeLevel ? ` – Grade ${profile.gradeLevel}` : '';
  const badgeText = `${roleName}${gradePart}`;

  return (
    <div>
      <button
        onClick={() => {
          if (backTo === 'CLOSE_MODAL' && onCloseModal) {
            onCloseModal();
          } else {
            navigate(backTo);
          }
        }}
        className="teal-button mb-4"
      >
        ← Back
      </button>
      <div className="profile-container">
        <ProfileNav isStudent={profileStudent} />
        <main className="profile-main">
          <ProfileHeader
            avatarUrl={avatarUrl}
            fullName={fullName}
            badgeText={badgeText}
            actionLabel={loginStudent ? 'Request Edit' : 'Edit Profile'}
            onActionClick={() => {
              if (backTo === 'CLOSE_MODAL' && onCloseModal) {
                onCloseModal();
              } else {
                navigate(`/profile/${user.id}/edit`, {
                  state: { user, from: backTo },
                });
              }
            }}
          />
          <section id="user-info">
            {profileStudent ? (
              <StudentProfile data={profile} user={user} />
            ) : (
              <NonStudentProfile data={profile} user={user} />
            )}
          </section>
        </main>
      </div>
    </div>
  );
};

export default ProfilePage;
