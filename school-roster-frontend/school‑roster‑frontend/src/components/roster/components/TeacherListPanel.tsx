// src/components/roster/components/TeacherListPanel.tsx
import React, { useEffect, useState } from 'react';
import { getAllTeachers } from '../../../services/userApi';
import type { UserListResponse } from '../../../types/User';
import '../styles/roster-update.css';

interface TeacherListPanelProps {
  initialTeacherId: string | null;
  onChange: (newTeacherId: string | null) => void;
}

export default function TeacherListPanel({
  initialTeacherId,
  onChange,
}: TeacherListPanelProps) {
  const [allTeachers, setAllTeachers] = useState<UserListResponse[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(initialTeacherId);

  useEffect(() => {
    (async () => {
      try {
        const resp = await getAllTeachers();
        setAllTeachers(resp.data);
      } catch (err) {
        console.error('Failed to load teachers', err);
      }
    })();
  }, []);

  useEffect(() => {
    onChange(selectedId);
  }, [selectedId, onChange]);

  // Filter out the current roster’s teacher
  const availableTeachers = initialTeacherId
    ? allTeachers.filter((t) => t.id !== initialTeacherId)
    : allTeachers;

  return (
    <div className="panel-container">
      <h3>Teacher</h3>

      {/* “No Change” option */}
      <div className="panel-item">
        <input
          type="radio"
          name="teacherRadio"
          checked={selectedId === null}
          onChange={() => setSelectedId(null)}
        />
        <div className="avatar-placeholder small" />
        <label>－ No Change</label>
      </div>

      {availableTeachers.map((t) => {
        const fullName = [t.firstName, t.middleName, t.lastName]
          .filter(Boolean)
          .join(' ');
        return (
          <div key={t.id} className="panel-item">
            <input
              type="radio"
              name="teacherRadio"
              checked={selectedId === t.id}
              onChange={() => setSelectedId(t.id)}
            />
            {t.photoUrl ? (
              <img
                src={t.photoUrl}
                alt={fullName || 'No name'}
                className="avatar-small"
              />
            ) : (
              <div className="avatar-placeholder small" />
            )}
            <label>
              {t.id} – {fullName || 'No Name'}
            </label>
          </div>
        );
      })}
    </div>
  );
}
