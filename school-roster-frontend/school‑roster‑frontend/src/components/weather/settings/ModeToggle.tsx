// src/components/weather/settings/ModeToggle.tsx
import React from 'react';

interface ModeToggleProps {
  mode: 'day' | 'weekly';
  onModeChange: (mode: 'day' | 'weekly') => void;
}

const ModeToggle: React.FC<ModeToggleProps> = ({ mode, onModeChange }) => (
  <div className="mb-4">
    <label className="block mb-1 font-medium text-black">Mode</label>
    <div className="flex gap-4">
      {(['day', 'weekly'] as const).map((m) => (
        <label key={m} className="inline-flex items-center text-black">
          <input
            type="radio"
            name="mode"
            value={m}
            checked={mode === m}
            onChange={() => onModeChange(m)}
            className="mr-2"
          />
          {m.charAt(0).toUpperCase() + m.slice(1)}
        </label>
      ))}
    </div>
  </div>
);

export default ModeToggle;
