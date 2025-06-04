/* eslint-disable @typescript-eslint/no-unused-vars */
/* src/components/roster/components/StudentListPanel.tsx */
import React, { useEffect, useState, useCallback } from 'react';
import { getAllStudents } from '../../../services/userApi';
import type { UserListResponse } from '../../../types/User';
import CreateStudentModal from './CreateStudentModal';
import '../styles/roster-update.css';

interface StudentListPanelProps {
  initialRosterStudentIds: string[];
  onAddChange: (newAddList: string[]) => void;
  onRemoveChange: (newRemoveList: string[]) => void;
}

export default function StudentListPanel({
  initialRosterStudentIds,
  onAddChange,
  onRemoveChange,
}: StudentListPanelProps) {
  const [allStudents, setAllStudents] = useState<UserListResponse[]>([]);
  const [mode, setMode] = useState<'add' | 'remove'>('add');
  const [addSelections, setAddSelections] = useState<string[]>([]);
  const [removeSelections, setRemoveSelections] = useState<string[]>([]);
  const [createModalOpen, setCreateModalOpen] = useState(false);

  // Helper to load (or reload) the full student list
  const loadStudents = useCallback(async () => {
    try {
      const resp = await getAllStudents();
      setAllStudents(resp.data);
      // Reset any selections after reloading
      setAddSelections([]);
      setRemoveSelections([]);
    } catch (err) {
      console.error('Failed to load students', err);
    }
  }, []);

  // On mount, load students once
  useEffect(() => {
    loadStudents();
  }, [loadStudents]);

  // Whenever addSelections changes, notify parent
  useEffect(() => {
    onAddChange(addSelections);
  }, [addSelections, onAddChange]);

  // Whenever removeSelections changes, notify parent
  useEffect(() => {
    onRemoveChange(removeSelections);
  }, [removeSelections, onRemoveChange]);

  // Partition students into “in roster” vs “not in roster”
  const inRoster = allStudents.filter((s) =>
    initialRosterStudentIds.includes(s.id)
  );
  const notInRoster = allStudents.filter(
    (s) => !initialRosterStudentIds.includes(s.id)
  );

  const toggleAdd = (id: string) => {
    setAddSelections((prev) =>
      prev.includes(id) ? prev.filter((sid) => sid !== id) : [...prev, id]
    );
  };

  const toggleRemove = (id: string) => {
    setRemoveSelections((prev) =>
      prev.includes(id) ? prev.filter((sid) => sid !== id) : [...prev, id]
    );
  };

  return (
    <div className="panel-container">
      <h3>Students</h3>

      {/* “Create Student” button */}
      <button
        className="create-student-btn"
        onClick={() => setCreateModalOpen(true)}
      >
        + Create Student
      </button>

      {/* Mode dropdown */}
      <div className="form-group">
        <label htmlFor="studentModeSelect">Action</label>
        <select
          id="studentModeSelect"
          value={mode}
          onChange={(e) => setMode(e.target.value as 'add' | 'remove')}
        >
          <option value="add">Add Student</option>
          <option value="remove">Remove Student</option>
        </select>
      </div>

      {mode === 'add' ? (
        <div className="list-section">
          <h4>Available to Add</h4>
          {notInRoster.length === 0 ? (
            <p className="empty-message">No available students.</p>
          ) : (
            notInRoster.map((s) => (
              <div key={s.id} className="panel-item">
                <input
                  type="checkbox"
                  checked={addSelections.includes(s.id)}
                  onChange={() => toggleAdd(s.id)}
                />
                {s.photoUrl ? (
                  <img src={s.photoUrl} alt={s.firstName} />
                ) : (
                  <div className="avatar-placeholder" />
                )}
                <label>
                  {s.id} – {s.firstName} {s.middleName} {s.lastName}
                </label>
              </div>
            ))
          )}
        </div>
      ) : (
        <div className="list-section">
          <h4>Currently in Roster</h4>
          {inRoster.length === 0 ? (
            <p className="empty-message">No students in this roster.</p>
          ) : (
            inRoster.map((s) => (
              <div key={s.id} className="panel-item">
                <input
                  type="checkbox"
                  checked={removeSelections.includes(s.id)}
                  onChange={() => toggleRemove(s.id)}
                />
                {s.photoUrl ? (
                  <img src={s.photoUrl} alt={s.firstName} />
                ) : (
                  <div className="avatar-placeholder" />
                )}
                <label>
                  {s.id} – {s.firstName} {s.middleName} {s.lastName}
                </label>
              </div>
            ))
          )}
        </div>
      )}

      {createModalOpen && (
        <CreateStudentModal
          isOpen={createModalOpen}
          onClose={() => {
            setCreateModalOpen(false);
            loadStudents(); // <— re‐fetch entire student list once modal closes
          }}
          onCreated={(newId) => {
            /* We could do partial updates here, but since onClose already
               triggers loadStudents, we don’t need to do anything extra */
          }}
          onAllDone={() => {
            // This fires once the profile has been saved & modal closed:
            setCreateModalOpen(false);
            loadStudents();
          }}
        />
      )}
    </div>
  );
}
