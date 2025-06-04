/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/roster/pages/UpdateRosterPage.tsx
import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  getRosterById,
  updateRoster,
  addStudents,
  removeStudents,
  reassignTeacher,
} from '../../../services/rosterApi';
import type {
  RosterResponse,
  UpdateRosterRequest,
} from '../../../types/Roster';
import RosterFieldsForm from '../components/RosterFieldsForm';
import StudentListPanel from '../components/StudentListPanel';
import TeacherListPanel from '../components/TeacherListPanel';
import '../styles/roster-update.css';

export default function UpdateRosterPage() {
  const { id } = useParams<{ id: string }>();
  const rosterId = Number(id);
  const navigate = useNavigate();

  const [roster, setRoster] = useState<RosterResponse | null>(null);

  // Form fields
  const [subjectName, setSubjectName] = useState('');
  const [period, setPeriod] = useState('');
  const [nickname, setNickname] = useState('');
  const [gradeLevel, setGradeLevel] = useState('');
  const [teacherId, setTeacherId] = useState('');

  // “Which panel” selector: 'students' or 'teacher'
  const [activePanel, setActivePanel] = useState<'students' | 'teacher'>(
    'students'
  );

  // Student-panel state
  const [studentsToAdd, setStudentsToAdd] = useState<string[]>([]);
  const [studentsToRemove, setStudentsToRemove] = useState<string[]>([]);

  // Teacher-panel state
  const [selectedTeacherId, setSelectedTeacherId] = useState<string | null>(
    null
  );

  // Error handling
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const resp = await getRosterById(rosterId);
        setRoster(resp.data);
        setSubjectName(resp.data.subjectName);
        setPeriod(resp.data.period);
        setNickname(resp.data.nickname);
        setGradeLevel(resp.data.gradeLevel);
        setTeacherId(resp.data.teacherId);
      } catch (err) {
        console.error('Failed to load roster', err);
      }
    })();
  }, [rosterId]);

  const handleSave = async () => {
    if (!roster) return;

    // 1) Update basic fields
    const updatePayload: UpdateRosterRequest = {
      rosterId,
      updatedRoster: {
        subjectName: subjectName.trim(),
        period: period.trim(),
        nickname: nickname.trim(),
        gradeLevel,
      },
    };

    try {
      await updateRoster(updatePayload);

      // 2) Add new students if any
      if (studentsToAdd.length > 0) {
        console.log('studnets to add ', studentsToAdd);
        await addStudents({ rosterId, studentId: studentsToAdd });
      }

      // 3) Remove students if any
      if (studentsToRemove.length > 0) {
        await removeStudents({ rosterId, studentId: studentsToRemove });
      }

      // 4) Reassign teacher if changed
      if (selectedTeacherId) {
        await reassignTeacher({ rosterId, newTeacherId: selectedTeacherId });
      }

      navigate('/roster');
    } catch (err: any) {
      console.error('Failed to save updates', err);
      setErrorMessage(
        err.response?.data?.message ||
          'Failed to update roster. Please try again.'
      );
    }
  };

  if (!roster) {
    return <p style={{ textAlign: 'center', marginTop: '40px' }}>Loading…</p>;
  }

  return (
    <div className="update-roster-container">
      <div className="header-bar">
        <h1>Update Roster</h1>
      </div>

      <div className="form-container">
        {errorMessage && <div className="error-banner">{errorMessage}</div>}

        <RosterFieldsForm
          subjectName={subjectName}
          onSubjectNameChange={setSubjectName}
          period={period}
          onPeriodChange={setPeriod}
          nickname={nickname}
          onNicknameChange={setNickname}
          gradeLevel={gradeLevel}
          onGradeLevelChange={setGradeLevel}
        />

        <div className="form-group">
          <label htmlFor="panelSelect">Which Panel?</label>
          <select
            id="panelSelect"
            className="panel-select"
            value={activePanel}
            onChange={(e) => {
              setErrorMessage(null);
              setActivePanel(e.target.value as 'students' | 'teacher');
            }}
          >
            <option value="students">Students</option>
            <option value="teacher">Teacher</option>
          </select>
        </div>

        {activePanel === 'students' && (
          <>
            <StudentListPanel
              initialRosterStudentIds={roster.students.map((s) => s.studentId)}
              onAddChange={setStudentsToAdd}
              onRemoveChange={setStudentsToRemove}
            />
          </>
        )}

        {activePanel === 'teacher' && (
          <TeacherListPanel
            initialTeacherId={teacherId}
            onChange={setSelectedTeacherId}
          />
        )}

        <div className="button-group">
          <button onClick={() => navigate('/roster')}>Cancel</button>
          <button onClick={handleSave}>Save Changes</button>
        </div>
      </div>
    </div>
  );
}
