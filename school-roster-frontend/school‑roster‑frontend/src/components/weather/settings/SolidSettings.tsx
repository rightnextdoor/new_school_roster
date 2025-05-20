// src/components/weather/settings/SolidSettings.tsx
import React from 'react';
import type { Settings } from '../SettingsModal';

interface SolidSettingsProps {
  solidColor: string;
  onSolidColorChange: (color: string) => void;

  overlayType: Settings['overlayType'];
  onOverlayTypeChange: (type: Settings['overlayType']) => void;

  overlaySettings: Settings['overlaySettings'];
  onOverlaySettingsChange: (
    updates: Partial<Settings['overlaySettings']>
  ) => void;
}

const SolidSettings: React.FC<SolidSettingsProps> = ({
  solidColor,
  onSolidColorChange,
  overlayType,
  onOverlayTypeChange,
  overlaySettings,
  onOverlaySettingsChange,
}) => (
  <div className="mb-4">
    {/* Solid color picker */}
    <input
      type="color"
      value={solidColor}
      onChange={(e) => onSolidColorChange(e.target.value)}
      className="w-full h-10 p-0 border-none mb-4"
    />

    {/* Pattern Overlay radios */}
    <label className="block mb-1 font-medium text-black">Pattern Overlay</label>
    <div className="flex gap-4 mb-2">
      {(
        [
          'none',
          'waves',
          'stripes',
          'circles',
          'blobs',
        ] as Settings['overlayType'][]
      ).map((po) => (
        <label key={po} className="inline-flex items-center text-black">
          <input
            type="radio"
            name="overlayType"
            value={po}
            checked={overlayType === po}
            onChange={() => onOverlayTypeChange(po)}
            className="mr-2"
          />
          {po.charAt(0).toUpperCase() + po.slice(1)}
        </label>
      ))}
    </div>

    {/* Detailed overlay controls */}
    {overlayType === 'waves' && (
      <>
        <label className="block mb-1 font-medium text-black">Orientation</label>
        <div className="flex gap-4 mb-2">
          {(['horizontal', 'vertical'] as const).map((o) => (
            <label key={o} className="inline-flex items-center text-black">
              <input
                type="radio"
                name="waveOrientation"
                value={o}
                checked={overlaySettings.orientation === o}
                onChange={() => onOverlaySettingsChange({ orientation: o })}
                className="mr-2"
              />
              {o.charAt(0).toUpperCase() + o.slice(1)}
            </label>
          ))}
        </div>
        <label className="block mb-1 text-black">
          Wave Height ({overlaySettings.height})
        </label>
        <input
          type="range"
          min="1"
          max="100"
          value={overlaySettings.height}
          onChange={(e) => onOverlaySettingsChange({ height: +e.target.value })}
          className="w-full mb-2"
        />
        <label className="block mb-1 text-black">
          Gap ({overlaySettings.frequency})
        </label>
        <input
          type="range"
          min="1"
          max="100"
          value={overlaySettings.frequency}
          onChange={(e) =>
            onOverlaySettingsChange({ frequency: +e.target.value })
          }
          className="w-full"
        />
      </>
    )}

    {overlayType === 'stripes' && (
      <>
        <label className="block mb-1 text-black">
          Angle ({overlaySettings.angle}°)
        </label>
        <input
          type="range"
          min="0"
          max="360"
          value={overlaySettings.angle}
          onChange={(e) => onOverlaySettingsChange({ angle: +e.target.value })}
          className="w-full mb-2"
        />
        <label className="block mb-1 text-black">
          Thickness ({overlaySettings.thickness}px)
        </label>
        <input
          type="range"
          min="1"
          max="100"
          value={overlaySettings.thickness}
          onChange={(e) =>
            onOverlaySettingsChange({ thickness: +e.target.value })
          }
          className="w-full"
        />
      </>
    )}

    {overlayType === 'circles' && (
      <>
        <label className="block mb-1 text-black">
          Circle Size ({overlaySettings.size}px)
        </label>
        <input
          type="range"
          min="5"
          max="100"
          value={overlaySettings.size}
          onChange={(e) => onOverlaySettingsChange({ size: +e.target.value })}
          className="w-full"
        />
      </>
    )}

    {overlayType === 'blobs' && (
      <>
        <label className="block mb-1 text-black">
          Blob Count ({overlaySettings.count})
        </label>
        <input
          type="range"
          min="1"
          max="20"
          value={overlaySettings.count}
          onChange={(e) => onOverlaySettingsChange({ count: +e.target.value })}
          className="w-full mb-2"
        />
        <label className="block mb-1 text-black">
          Size Variance ({overlaySettings.sizeVariance})
        </label>
        <input
          type="range"
          min="1"
          max="100"
          value={overlaySettings.sizeVariance}
          onChange={(e) =>
            onOverlaySettingsChange({ sizeVariance: +e.target.value })
          }
          className="w-full"
        />
      </>
    )}
  </div>
);

export default SolidSettings;
