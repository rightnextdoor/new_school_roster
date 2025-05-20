// src/components/weather/widget/SettingsButton.tsx
import React from 'react';

interface SettingsButtonProps {
  color: string;
  onClick: () => void;
}

const SettingsButton: React.FC<SettingsButtonProps> = ({ color, onClick }) => (
  <button
    type="button"
    aria-label="Open settings"
    className="absolute top-4 right-4 z-10"
    style={{ color }}
    onClick={onClick}
  >
    ⚙
  </button>
);

export default SettingsButton;
