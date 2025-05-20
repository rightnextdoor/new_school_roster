// src/components/weather/settings/AnimationToggle.tsx
import React from 'react';

interface AnimationToggleProps {
  animations: boolean;
  onAnimationsChange: (enabled: boolean) => void;
}

const AnimationToggle: React.FC<AnimationToggleProps> = ({
  animations,
  onAnimationsChange,
}) => (
  <div className="mb-4">
    <label className="inline-flex items-center text-black">
      <input
        type="checkbox"
        checked={animations}
        onChange={(e) => onAnimationsChange(e.target.checked)}
        className="mr-2"
      />
      Enable animations
    </label>
  </div>
);

export default AnimationToggle;
