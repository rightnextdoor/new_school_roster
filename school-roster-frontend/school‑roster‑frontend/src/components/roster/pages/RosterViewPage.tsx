/* src/components/roster/pages/RosterViewPage.tsx */
/* eslint-disable @typescript-eslint/no-explicit-any */

import React, { useEffect, useState } from 'react';
import { getRosterById } from '../../../services/rosterApi';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../../contexts/AuthContext';
import '../styles/roster-view.css';
import type { RosterResponse, StudentInfo } from '../../../types/Roster';

// Placeholder Graph component.
// You can replace this with your reusable graph component (line/bar/pie, etc.).
function RosterChartPlaceholder({ isTeacher }: { isTeacher: boolean }) {
  const [chartType, setChartType] = useState<'line' | 'bar' | 'pie'>('bar');

  return (
    <div className="graph-container">
      <div className="graph-controls">
        <label>
          Chart Type:{' '}
          <select
            value={chartType}
            onChange={(e) => setChartType(e.target.value as any)}
          >
            <option value="line">Line</option>
            <option value="bar">Bar</option>
            <option value="pie">Pie</option>
          </select>
        </label>
        {isTeacher && (
          <label>
            View:{' '}
            <select>
              <option value="all">All Students</option>
              <option value="gpaQuarterly">Roster GPA (Quarterly)</option>
              {/* add more teacher-specific options here */}
            </select>
          </label>
        )}
      </div>
      <div className="graph-placeholder">
        <p>
          {isTeacher
            ? `Teacher’s ${chartType.toUpperCase()} Chart (placeholder)`
            : `Student’s ${chartType.toUpperCase()} Chart (placeholder)`}
        </p>
      </div>
    </div>
  );
}

export default function RosterViewPage() {
  const { id } = useParams<{ id: string }>();
  const rosterId = Number(id);
  const [roster, setRoster] = useState<RosterResponse | null>(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();
  const { user, authLoading } = useAuth();
  const roles = user?.roles ?? [];
  const isTeacher = roles.includes('TEACHER') || roles.includes('TEACHER_LEAD');

  useEffect(() => {
    async function fetchRoster() {
      try {
        const res = await getRosterById(rosterId);
        setRoster(res.data);
      } catch (err) {
        console.error('Failed to load roster', err);
      } finally {
        setLoading(false);
      }
    }

    if (!authLoading) {
      fetchRoster();
    }
  }, [rosterId, authLoading]);

  // 1) While waiting for auth or roster data, show a loading placeholder
  if (authLoading || loading) {
    return <div className="view-loading">Loading…</div>;
  }

  // 2) If roster fetch failed or returned null
  if (!roster) {
    return (
      <div className="view-error">
        <p>Roster not found.</p>
        <button onClick={() => navigate('/roster')}>Back to Overview</button>
      </div>
    );
  }

  const fullTeacherName = [
    roster.teacherFirstName,
    roster.teacherMiddleName,
    roster.teacherLastName,
  ]
    .filter(Boolean)
    .join(' ');

  return (
    <div className="view-container">
      {/* 1) Teacher Section */}
      <div className="teacher-section">
        {roster.teacherPhoto ? (
          <img
            src={roster.teacherPhoto}
            alt={fullTeacherName}
            className="teacher-photo"
          />
        ) : (
          <div className="teacher-photo placeholder" />
        )}
        <div className="teacher-info">
          <h2 className="teacher-name">{fullTeacherName}</h2>
          <p className="teacher-role">Teacher</p>
        </div>
        <button className="back-button" onClick={() => navigate('/roster')}>
          ← Back to Overview
        </button>
      </div>

      {/* 2) Roster Info */}
      <div className="roster-info">
        <div>
          <span className="info-label">Grade Level:</span>{' '}
          <span className="info-value">{roster.gradeLevel}</span>
        </div>
        <div>
          <span className="info-label">Subject:</span>{' '}
          <span className="info-value">{roster.subjectName}</span>
        </div>
        <div>
          <span className="info-label">Section / Nickname:</span>{' '}
          <span className="info-value">{roster.nickname}</span>
        </div>
        <div>
          <span className="info-label">Period:</span>{' '}
          <span className="info-value">{roster.period}</span>
        </div>
      </div>

      {/* 3) Main Content: Student List on left, Graph on right */}
      <div className="content-area">
        <aside className="student-list">
          <h3 className="student-list-title">Enrolled Students</h3>
          <div className="student-list-container">
            {roster.students.length === 0 ? (
              <p className="empty-message">No students enrolled.</p>
            ) : (
              roster.students.map((s: StudentInfo) => {
                const fullName = [s.firstName, s.middleName, s.lastName]
                  .filter(Boolean)
                  .join(' ');
                return (
                  <div key={s.studentId} className="student-item">
                    {s.studentPhoto ? (
                      <img
                        src={s.studentPhoto}
                        alt={fullName}
                        className="student-photo"
                      />
                    ) : (
                      <div className="student-photo placeholder" />
                    )}
                    <div className="student-details">
                      <p className="student-name">{fullName}</p>
                      <p className="student-id">ID: {s.studentId}</p>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </aside>

        <section className="graph-area">
          <RosterChartPlaceholder isTeacher={isTeacher} />
        </section>
      </div>
    </div>
  );
}
