// src/components/roster/pages/RosterOverviewPage.tsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../../../contexts/AuthContext';
import {
  getAllRosters,
  getRostersByTeacher,
  getRostersByStudent,
  deleteRoster,
} from '../../../services/rosterApi';
import RosterSelectorModal from '../components/RosterSelectModal';
import { RosterResponse } from '../../../types/Roster';
import { useNavigate } from 'react-router-dom';
import '../styles/roster.css';

export default function RosterOverviewPage() {
  // Hooks must always run at the top
  const { user, authLoading } = useAuth();
  const [rosters, setRosters] = useState<RosterResponse[]>([]);
  const navigate = useNavigate();
  const roles = user?.roles ?? [];
  const isStudent = roles.includes('STUDENT');

  const [selectorOpen, setSelectorOpen] = useState(false);

  // Fetch once authLoading is false
  useEffect(() => {
    if (!authLoading) {
      const fetchRosters = async () => {
        try {
          let res;
          if (
            roles.some((r) =>
              ['ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR'].includes(r)
            )
          ) {
            res = await getAllRosters();
          } else if (
            roles.includes('TEACHER') ||
            roles.includes('TEACHER_LEAD')
          ) {
            res = await getRostersByTeacher();
          } else {
            res = await getRostersByStudent();
          }
          setRosters(res.data);
        } catch (err) {
          console.error('Failed to fetch rosters', err);
        }
      };
      fetchRosters();
    }
  }, [authLoading, roles]);

  // While loading user, show a placeholder
  if (authLoading) {
    return <p>Loading...</p>;
  }

  return (
    <div className="roster-container">
      {/* Teal header bar (full width) */}
      <div className="header-bar">
        <h1>Roster Overview</h1>
      </div>

      {/* The white “button” zone & table zone share the same #f5f6f8 background */}
      <div className="roster-content">
        {!isStudent && (
          <div className="button-container">
            <button onClick={() => navigate('/roster/create')}>
              Create Roster
            </button>
            <button onClick={() => setSelectorOpen(true)}>Update Roster</button>
          </div>
        )}

        <div className="table-container">
          <table>
            <thead>
              <tr>
                <th>Teacher Name</th>
                <th>Subject</th>
                <th>Period</th>
                <th>Section</th>
                <th># Students</th>
                <th>Class GPA</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {rosters.map((r) => (
                <tr key={r.rosterId}>
                  <td>
                    {r.teacherFirstName} {r.teacherLastName}
                  </td>
                  <td>{r.subjectName}</td>
                  <td>{r.period}</td>
                  <td>{r.nickname}</td>
                  <td>{r.students.length}</td>
                  <td className="gpa">{r.classGpa.toFixed(2)}</td>
                  <td>
                    <button
                      onClick={() => navigate(`/roster/view/${r.rosterId}`)}
                    >
                      View Roster
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      {/* RosterSelectorModal */}
      {!isStudent && (
        <RosterSelectorModal
          rosters={rosters}
          isOpen={selectorOpen}
          onClose={() => setSelectorOpen(false)}
          onSelectToUpdate={(id) => navigate(`/roster/update/${id}`)}
          onSelectToDelete={async (id) => {
            try {
              await deleteRoster(id);
              let refreshed;
              if (
                roles.some((r) =>
                  ['ADMIN', 'ADMINISTRATOR', 'OFFICE_ADMINISTRATOR'].includes(r)
                )
              ) {
                refreshed = await getAllRosters();
              } else if (
                roles.includes('TEACHER') ||
                roles.includes('TEACHER_LEAD')
              ) {
                refreshed = await getRostersByTeacher();
              } else {
                refreshed = await getRostersByStudent();
              }
              setRosters(refreshed.data);
            } catch (err) {
              console.error('Failed to delete roster', err);
            }
          }}
        />
      )}
    </div>
  );
}
