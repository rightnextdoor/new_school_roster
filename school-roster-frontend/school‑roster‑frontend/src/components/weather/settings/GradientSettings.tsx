// src/components/weather/settings/GradientSettings.tsx
import React from 'react';
import type { Settings } from '../SettingsModal';

interface GradientSettingsProps {
  gradientStart: string;
  gradientEnd: string;
  blendType: Settings['blendType'];
  onGradientStartChange: (color: string) => void;
  onGradientEndChange: (color: string) => void;
  onBlendTypeChange: (type: Settings['blendType']) => void;
}

const GradientSettings: React.FC<GradientSettingsProps> = ({
  gradientStart,
  gradientEnd,
  blendType,
  onGradientStartChange,
  onGradientEndChange,
  onBlendTypeChange,
}) => (
  <div className="mb-4">
    {/* Start/End color pickers */}
    <div className="flex gap-2 mb-2">
      <input
        type="color"
        value={gradientStart}
        onChange={(e) => onGradientStartChange(e.target.value)}
        className="w-1/2 h-10 p-0 border-none"
      />
      <input
        type="color"
        value={gradientEnd}
        onChange={(e) => onGradientEndChange(e.target.value)}
        className="w-1/2 h-10 p-0 border-none"
      />
    </div>

    {/* Blend type radios */}
    <div className="flex gap-4 mb-4">
      {(['linear', 'radial', 'conic'] as Settings['blendType'][]).map((bt) => (
        <label key={bt} className="inline-flex items-center text-black">
          <input
            type="radio"
            name="blendType"
            value={bt}
            checked={blendType === bt}
            onChange={() => onBlendTypeChange(bt)}
            className="mr-2"
          />
          {bt.charAt(0).toUpperCase() + bt.slice(1)}
        </label>
      ))}
    </div>
  </div>
);

export default GradientSettings;
