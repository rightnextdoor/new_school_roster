// src/components/roster/components/RosterSelectorModal.tsx
import React, { useState, useEffect } from 'react';
import ReactDOM from 'react-dom';
import type { RosterResponse } from '../../../types/Roster';
import '../styles/roster-selector.css';

interface Props {
  /** List of all rosters fetched from the backend */
  rosters: RosterResponse[];
  /** Whether the modal is currently open */
  isOpen: boolean;
  /** Called to close the modal without selecting anything */
  onClose: () => void;
  /** Called with the selected rosterId when “Update” is clicked */
  onSelectToUpdate: (rosterId: number) => void;
  /** Called with the selected rosterId when “Delete” is clicked */
  onSelectToDelete: (rosterId: number) => void;
}

export default function RosterSelectorModal({
  rosters,
  isOpen,
  onClose,
  onSelectToUpdate,
  onSelectToDelete,
}: Props) {
  // 1) Keep track of which rosterId is currently selected in the dropdown.
  //    Initialize to the first roster’s ID (if any), or 0.
  const [selectedId, setSelectedId] = useState<number>(
    rosters.length > 0 ? rosters[0].rosterId : 0
  );

  // 2) If the `rosters` array changes (e.g. after you delete one),
  //    reinitialize `selectedId` to the first element (or 0).
  useEffect(() => {
    if (rosters.length > 0) {
      setSelectedId(rosters[0].rosterId);
    } else {
      setSelectedId(0);
    }
  }, [rosters]);

  if (!isOpen) return null;

  return ReactDOM.createPortal(
    <div className="modal-overlay">
      <div className="modal-dialog">
        <h3>Select a Roster to Update or Delete</h3>

        <div className="form-group">
          <label htmlFor="rosterSelect">Roster</label>
          <select
            id="rosterSelect"
            className="roster-dropdown"
            value={selectedId}
            onChange={(e) => {
              // Convert the string e.target.value back into a number
              setSelectedId(Number(e.target.value));
            }}
          >
            {rosters.map((r) => (
              <option key={r.rosterId} value={r.rosterId}>
                {/* Display text for each option */}
                {r.subjectName} – {r.nickname} (Period: {r.period})
              </option>
            ))}
          </select>
        </div>

        <div className="button-row">
          <button
            type="button"
            className="btn-cancel"
            onClick={() => {
              onClose();
            }}
          >
            Cancel
          </button>
          <button
            type="button"
            className="btn-delete"
            onClick={() => {
              // Pass the currently selected rosterId to onSelectToDelete
              onSelectToDelete(selectedId);
              onClose();
            }}
          >
            Delete
          </button>
          <button
            type="button"
            className="btn-update"
            onClick={() => {
              // Pass the currently selected rosterId to onSelectToUpdate
              onSelectToUpdate(selectedId);
              onClose();
            }}
          >
            Update
          </button>
        </div>
      </div>
    </div>,
    document.body
  );
}
