// src/components/weather/widget/DetailsGrid.tsx
import React from 'react';

interface DetailsGridProps {
  color: string;
}

const DetailsGrid: React.FC<DetailsGridProps> = ({ color }) => (
  <div className="grid grid-cols-2 gap-4">
    {/* UV Index */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">☀️</span>
        <h4 className="font-medium" style={{ color }}>
          UV Index
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        Moderate right now
      </p>
      <p className="text-3xl font-semibold mt-auto" style={{ color }}>
        4
      </p>
    </div>

    {/* Humidity */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">💧</span>
        <h4 className="font-medium" style={{ color }}>
          Humidity
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        Lower than yesterday
      </p>
      <p className="text-3xl font-semibold mt-auto" style={{ color }}>
        64%
      </p>
    </div>

    {/* Wind with Compass */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">💨</span>
        <h4 className="font-medium" style={{ color }}>
          Wind
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        There is a light breeze
      </p>
      <div className="relative mt-auto flex flex-col items-center justify-center h-32">
        {/* Compass Circle with number and arrow */}
        <div className="relative w-24 h-24 rounded-full border-2 border-white/50 flex flex-col items-center justify-center">
          <p className="text-lg font-semibold" style={{ color }}>
            8 mph
          </p>
          {(() => {
            const windDeg = 180; // placeholder degrees
            return (
              <span
                className="text-xl mt-1"
                style={{ color, transform: `rotate(${windDeg}deg)` }}
              >
                ↑
              </span>
            );
          })()}
        </div>
        {/* Cardinal Labels */}
        <span
          className="absolute -top-2 left-1/2 transform -translate-x-1/2 text-xs"
          style={{ color }}
        >
          N
        </span>
        <span
          className="absolute -right-2 top-1/2 transform -translate-y-1/2 text-xs"
          style={{ color }}
        >
          E
        </span>
        <span
          className="absolute -bottom-2 left-1/2 transform -translate-x-1/2 text-xs"
          style={{ color }}
        >
          S
        </span>
        <span
          className="absolute -left-2 top-1/2 transform -translate-y-1/2 text-xs"
          style={{ color }}
        >
          W
        </span>
      </div>
    </div>

    {/* Dew Point */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">🌡️</span>
        <h4 className="font-medium" style={{ color }}>
          Dew Point
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        Noticeable humidity
      </p>
      <p className="text-3xl font-semibold mt-auto" style={{ color }}>
        57°
      </p>
    </div>

    {/* Pressure */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">📈</span>
        <h4 className="font-medium" style={{ color }}>
          Pressure
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        Currently rising
      </p>
      <p className="text-3xl font-semibold mt-auto" style={{ color }}>
        30.12 in
      </p>
    </div>

    {/* Visibility */}
    <div className="bg-black/10 rounded-lg p-4 flex flex-col">
      <div className="flex items-center mb-2">
        <span className="text-2xl mr-2">👁️</span>
        <h4 className="font-medium" style={{ color }}>
          Visibility
        </h4>
      </div>
      <p className="text-sm opacity-80 mb-4" style={{ color }}>
        Unlimited visibility
      </p>
      <p className="text-3xl font-semibold mt-auto" style={{ color }}>
        Unlimited
      </p>
    </div>
  </div>
);

export default DetailsGrid;
