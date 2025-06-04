// src/components/roster/components/StudentProfileModal.tsx
import React, { useEffect, useState } from 'react';
import { getUserById } from '../../../services/userApi';
import ProfilePage from '../../profile/ProfilePage';
import '../styles/roster.css';

interface StudentProfileModalProps {
  userId: string;
  visible: boolean;
  onClose: () => void;
}

export default function StudentProfileModal({
  userId,
  visible,
  onClose,
}: StudentProfileModalProps) {
  const [user, setUser] = useState<{
    id: string;
    email: string;
    roles: string[];
  } | null>(null);
  const [loading, setLoading] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);

  useEffect(() => {
    if (!visible) return;
    setLoading(true);
    getUserById(userId)
      .then((u) => {
        setUser(u);
      })
      .catch((err) => {
        console.error('Failed to load user', err);
        setLoadError('Unable to load user data.');
      })
      .finally(() => setLoading(false));
  }, [visible, userId]);

  if (!visible) return null;

  return (
    <div className="roster-modal-overlay">
      <div
        className="roster-modal"
        style={{
          width: '90%',
          maxWidth: '800px',
          height: '90%',
          overflow: 'auto',
        }}
      >
        {loading ? (
          <p>Loading user...</p>
        ) : loadError ? (
          <p style={{ color: 'red' }}>{loadError}</p>
        ) : user ? (
          // Pass onClose as onCloseModal so ProfilePage can close the modal when backTo="CLOSE_MODAL"
          <ProfilePage
            user={user}
            editMode={true}
            backTo="CLOSE_MODAL"
            onCloseModal={onClose}
          />
        ) : null}

        <button
          className="roster-button cancel"
          style={{ position: 'absolute', top: 16, right: 16 }}
          onClick={onClose}
        >
          Close
        </button>
      </div>
    </div>
  );
}
