/* eslint-disable @typescript-eslint/no-explicit-any */
// src/components/roster/pages/RosterOverviewPage.tsx
import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllRosters, deleteRoster } from '../../../services/rosterApi';
import type { RosterResponse } from '../../../types/Roster';
import '../styles/roster.css';
import '../styles/roster-update.css'; // for modal styling

export default function RosterOverviewPage() {
  const navigate = useNavigate();
  const [rosters, setRosters] = useState<RosterResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [selectedRosterId, setSelectedRosterId] = useState<number | null>(null);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    (async () => {
      try {
        const resp = await getAllRosters();
        setRosters(resp.data);
      } catch (err) {
        console.error('Failed to load rosters', err);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const openUpdateModal = () => {
    setErrorMessage(null);
    setSelectedRosterId(null);
    setModalOpen(true);
  };

  const closeModal = () => {
    setErrorMessage(null);
    setModalOpen(false);
  };

  const handleDelete = async () => {
    if (!selectedRosterId) {
      setErrorMessage('Please choose a roster first.');
      return;
    }
    try {
      await deleteRoster(selectedRosterId);
      // Refresh the list
      const resp = await getAllRosters();
      setRosters(resp.data);
      closeModal();
    } catch (err: any) {
      console.error('Delete failed', err);
      setErrorMessage(
        err.response?.data?.message || 'Failed to delete roster.'
      );
    }
  };

  const handleUpdateClick = () => {
    if (!selectedRosterId) {
      setErrorMessage('Please choose a roster first.');
      return;
    }
    closeModal();
    navigate(`/roster/update/${selectedRosterId}`);
  };

  if (loading) {
    return <p style={{ textAlign: 'center', marginTop: '40px' }}>Loading…</p>;
  }

  return (
    <>
      <div className="header-bar">
        <h1>Roster Overview</h1>
      </div>

      <div className="button-container">
        <button onClick={() => navigate('/roster/create')}>
          Create Roster
        </button>
        <button onClick={openUpdateModal}>Update Roster</button>
      </div>

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

      {modalOpen && (
        <div className="modal-backdrop">
          <div className="modal-dialog">
            <h2>Choose a Roster to Update/Delete</h2>

            <div className="form-group">
              <label htmlFor="rosterSelect">Select Roster</label>
              <select
                id="rosterSelect"
                value={selectedRosterId ?? ''}
                onChange={(e) => {
                  setErrorMessage(null);
                  setSelectedRosterId(Number(e.target.value));
                }}
              >
                <option value="" disabled>
                  -- pick one --
                </option>
                {rosters.map((r) => (
                  <option key={r.rosterId} value={r.rosterId}>
                    {`${r.subjectName} – ${r.period} (${r.nickname})`}
                  </option>
                ))}
              </select>
            </div>

            {errorMessage && (
              <div className="error-banner" style={{ marginTop: '12px' }}>
                {errorMessage}
              </div>
            )}

            <div className="button-group" style={{ marginTop: '16px' }}>
              <button onClick={closeModal}>Cancel</button>
              <button onClick={handleDelete}>Delete</button>
              <button onClick={handleUpdateClick}>Update</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
