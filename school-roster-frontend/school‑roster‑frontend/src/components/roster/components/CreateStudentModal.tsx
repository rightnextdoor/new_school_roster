/* eslint-disable @typescript-eslint/no-explicit-any */
import React, { useState, useEffect } from 'react';
import { createUser, getUserById, User } from '../../../services/userApi';
import ProfilePage from '../../profile/ProfilePage';
import '../styles/roster-update.css';

interface CreateStudentModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreated: (newUserId: string) => void;
  onAllDone: () => void;
}

export default function CreateStudentModal({
  isOpen,
  onClose,
  onCreated,
  onAllDone,
}: CreateStudentModalProps) {
  const [step, setStep] = useState<'form' | 'profile'>('form');
  const [email, setEmail] = useState('');
  const [confirmEmail, setConfirmEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [newUserObj, setNewUserObj] = useState<User | null>(null);

  // Reset whenever the modal closes
  useEffect(() => {
    if (isOpen) {
      document.body.classList.add('no-scroll');
    } else {
      document.body.classList.remove('no-scroll');
      // reset state when fully closed:
      setStep('form');
      setEmail('');
      setConfirmEmail('');
      setPassword('');
      setConfirmPassword('');
      setErrorMessage(null);
      setSubmitting(false);
      setNewUserObj(null);
    }
    return () => {
      document.body.classList.remove('no-scroll');
    };
  }, [isOpen]);

  if (!isOpen) return null;

  // 1) Validate form
  const validateForm = (): boolean => {
    if (!email.trim() || !confirmEmail.trim()) {
      setErrorMessage('Both email fields are required.');
      return false;
    }
    if (email.trim() !== confirmEmail.trim()) {
      setErrorMessage('Emails do not match.');
      return false;
    }
    if (!password || !confirmPassword) {
      setErrorMessage('Both password fields are required.');
      return false;
    }
    if (password !== confirmPassword) {
      setErrorMessage('Passwords do not match.');
      return false;
    }
    setErrorMessage(null);
    return true;
  };

  // 2) Create user, then fetch full object → show ProfilePage
  const handleCreate = async () => {
    if (submitting) return;
    if (!validateForm()) return;

    setSubmitting(true);
    try {
      const resp = await createUser({
        email: email.trim(),
        password,
        roles: ['STUDENT'],
      });
      const created: User = resp.data;
      onCreated(created.id);

      // Fetch full user for ProfilePage:
      const userResp = await getUserById(created.id);
      setNewUserObj(userResp);

      setStep('profile');
    } catch (err: any) {
      console.error(err);
      setErrorMessage(
        err.response?.data?.message || 'Failed to create student.'
      );
      setSubmitting(false);
    }
  };

  // 3) If “profile” step, embed ProfilePage inside modal. Pass backTo="CLOSE_MODAL"
  if (step === 'profile' && newUserObj) {
    return (
      <div className="modal-backdrop">
        <div className="modal-dialog modal-large">
          <ProfilePage
            user={newUserObj}
            editMode={true}
            backTo="CLOSE_MODAL"
            onCloseModal={() => {
              onClose();
              // Inform parent that the entire flow is done
              onAllDone();
            }}
          />
        </div>
      </div>
    );
  }

  // 4) Otherwise show the “create student” form
  return (
    <div className="modal-backdrop">
      <div className="modal-dialog">
        <h2>Create New Student</h2>
        {errorMessage && <div className="error-banner">{errorMessage}</div>}

        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="confirmEmail">Confirm Email</label>
          <input
            type="email"
            id="confirmEmail"
            value={confirmEmail}
            onChange={(e) => setConfirmEmail(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>

        <div className="form-group">
          <label htmlFor="confirmPassword">Confirm Password</label>
          <input
            type="password"
            id="confirmPassword"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
          />
        </div>

        <div className="button-group">
          <button onClick={onClose}>Cancel</button>
          <button onClick={handleCreate}>
            {submitting ? 'Creating…' : 'Create Student'}
          </button>
        </div>
      </div>
    </div>
  );
}
