// src/components/profile/ProfileModal.tsx

import React from 'react';
import './profile.css';

interface Action {
  label: string;
  onClick: () => void;
}

interface Props {
  isOpen: boolean;
  title: string;
  message: string;
  primaryAction?: Action;
  secondaryAction: Action;
}

const ProfileModal: React.FC<Props> = ({
  isOpen,
  title,
  message,
  primaryAction,
  secondaryAction,
}) => {
  if (!isOpen) return null;
  return (
    <div className="modal-backdrop">
      <div className="modal-dialog" role="dialog" aria-modal="true">
        <h2>{title}</h2>
        <p>{message}</p>
        <div className="modal-actions">
          {primaryAction && (
            <button className="teal-button" onClick={primaryAction.onClick}>
              {primaryAction.label}
            </button>
          )}
          <button onClick={secondaryAction.onClick}>
            {secondaryAction.label}
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProfileModal;
