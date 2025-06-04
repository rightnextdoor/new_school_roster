/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/roster/pages/CreateRosterPage.tsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import { createRoster } from '../../../services/rosterApi';
import '../styles/roster‐create.css';

export default function CreateRosterPage() {
  const { user, authLoading } = useAuth();
  const navigate = useNavigate();

  // Form fields
  const [subjectName, setSubjectName] = useState('');
  const [period, setPeriod] = useState('');
  const [nickname, setNickname] = useState('');
  const [gradeLevel, setGradeLevel] = useState(''); // Now a text field

  // Validation / error state
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  // If auth is still loading, show a placeholder
  useEffect(() => {
    if (!authLoading && !user) {
      // If user is not logged in, redirect back to dashboard/login
      navigate('/dashboard');
    }
  }, [authLoading, user, navigate]);

  if (authLoading) {
    return <p style={{ textAlign: 'center', marginTop: '48px' }}>Loading...</p>;
  }

  // Simple front‐end validation:
  const validateForm = (): boolean => {
    if (!subjectName.trim()) {
      setErrorMessage('Subject Name is required.');
      return false;
    }
    if (!period.trim()) {
      setErrorMessage('Period is required.');
      return false;
    }
    if (!nickname.trim()) {
      setErrorMessage('Section / Nickname is required.');
      return false;
    }
    if (!gradeLevel.trim()) {
      setErrorMessage('Grade Level is required.');
      return false;
    }
    setErrorMessage(null);
    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (submitting) return;

    if (!validateForm()) return;

    setSubmitting(true);
    try {
      // Call the backend to create a roster
      // API expects: { subjectName, period, nickname, gradeLevel }
      await createRoster({
        subjectName: subjectName.trim(),
        period: period.trim(),
        nickname: nickname.trim(),
        gradeLevel: gradeLevel.trim(),
      });
      // On success, navigate back to overview:
      navigate('/roster');
    } catch (err: any) {
      console.error('Failed to create roster', err);
      const msg =
        err.response?.data?.message ||
        'Failed to create roster. Please try again.';
      setErrorMessage(msg);
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    navigate('/roster');
  };

  return (
    <div className="main-container">
      <div className="form-header">
        <h2>Create New Roster</h2>
      </div>

      <div className="form-content">
        {errorMessage && (
          <div className="error-banner" style={{ marginBottom: '16px' }}>
            {errorMessage}
          </div>
        )}

        <form onSubmit={handleSubmit} noValidate>
          <div className="form-group">
            <label htmlFor="subjectName">Subject Name</label>
            <input
              type="text"
              id="subjectName"
              name="subjectName"
              placeholder="Enter subject name"
              value={subjectName}
              onChange={(e) => setSubjectName(e.target.value)}
              disabled={submitting}
            />
          </div>

          <div className="form-group">
            <label htmlFor="period">Period</label>
            <input
              type="text"
              id="period"
              name="period"
              placeholder="Enter period (e.g. 3rd Quarter)"
              value={period}
              onChange={(e) => setPeriod(e.target.value)}
              disabled={submitting}
            />
          </div>

          <div className="form-group">
            <label htmlFor="nickname">Section / Nickname</label>
            <input
              type="text"
              id="nickname"
              name="nickname"
              placeholder="Enter section or nickname"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              disabled={submitting}
            />
          </div>

          <div className="form-group">
            <label htmlFor="gradeLevel">Grade Level</label>
            <input
              type="text"
              id="gradeLevel"
              name="gradeLevel"
              placeholder="Enter grade level (e.g. 3A, K, 10)"
              value={gradeLevel}
              onChange={(e) => setGradeLevel(e.target.value)}
              disabled={submitting}
            />
          </div>

          <div className="action-buttons">
            <button type="button" onClick={handleCancel} disabled={submitting}>
              Cancel
            </button>
            <button type="submit" disabled={submitting}>
              {submitting ? 'Creating…' : 'Create Roster'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
