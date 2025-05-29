// src/components/profile/ProfileHeader.tsx

import React from 'react';

interface Props {
  avatarUrl?: string;
  fullName: string;
  badgeText: string;
  actionLabel: string;
  onActionClick: () => void;
}

const ProfileHeader: React.FC<Props> = ({
  avatarUrl,
  fullName,
  badgeText,
  actionLabel,
  onActionClick,
}) => (
  <div className="profile-header">
    {avatarUrl ? (
      <img src={avatarUrl} alt={fullName} className="avatar" />
    ) : (
      <div className="avatar" />
    )}
    <div className="profile-details">
      <h2>{fullName}</h2>
      <span className="badge">{badgeText}</span>
    </div>
    <button className="teal-button" onClick={onActionClick}>
      {actionLabel}
    </button>
  </div>
);

export default ProfileHeader;
