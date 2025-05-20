// src/components/weather/settings/UnitToggle.tsx
import React from 'react';

interface UnitToggleProps {
  unit: '°F' | '°C';
  onUnitChange: (unit: '°F' | '°C') => void;
}

const UnitToggle: React.FC<UnitToggleProps> = ({ unit, onUnitChange }) => (
  <div className="mb-4">
    <label className="block mb-1 font-medium text-black">Units</label>
    <div className="flex gap-4">
      {['°F', '°C'].map((u) => (
        <label key={u} className="inline-flex items-center text-black">
          <input
            type="radio"
            name="unit"
            value={u}
            checked={unit === u}
            onChange={() => onUnitChange(u as '°F' | '°C')}
            className="mr-2"
          />
          {u}
        </label>
      ))}
    </div>
  </div>
);

export default UnitToggle;
