// src/components/weather/SettingsModal.tsx
import React, { useState, useEffect } from 'react';
import LocationSearch from './settings/LocationSearch';
import UnitToggle from './settings/UnitToggle';
// import ModeToggle from './settings/ModeToggle';
import AnimationToggle from './settings/AnimationToggle';
import SolidSettings from './settings/SolidSettings';
import GradientSettings from './settings/GradientSettings';

export interface Settings {
  citySearch: string;
  unit: '°F' | '°C';
  mode: 'day' | 'weekly';
  animations: boolean;
  bgStyle: 'animated' | 'solid' | 'gradient';
  solidColor: string;
  gradientStart: string;
  gradientEnd: string;
  blendType: 'linear' | 'radial' | 'conic';
  overlayType: 'none' | 'waves' | 'stripes' | 'circles' | 'blobs';
  overlaySettings: {
    height: number;
    frequency: number;
    orientation: 'horizontal' | 'vertical';
    angle: number;
    thickness: number;
    size: number;
    count: number;
    sizeVariance: number;
  };
}

interface SettingsModalProps {
  isOpen: boolean;
  settings: Settings;
  onSettingsChange: (s: Settings) => void;
  /** Called when the user clicks Search */
  onSearch: () => void;
  /** Any error message from the last search attempt */
  searchError?: string | null;
  onClose: () => void;
}

const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  settings,
  onSettingsChange,
  onSearch,
  searchError,
  onClose,
}) => {
  const [localCity, setLocalCity] = useState(settings.citySearch);

  // Keep localCity in sync with settings.citySearch if it changes externally
  useEffect(() => {
    setLocalCity(settings.citySearch);
  }, [settings.citySearch]);

  if (!isOpen) return null;

  const handleSearchClick = () => {
    // 1) Push the local city value into the top-level settings...
    onSettingsChange({ ...settings, citySearch: localCity });
    // 2) ...then invoke the parent search handler
    onSearch();
  };

  const update = (changes: Partial<Settings>) => {
    onSettingsChange({ ...settings, ...changes });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-black/50" onClick={onClose} />

      <div
        className="relative p-6 rounded-2xl shadow-xl w-11/12 max-w-md bg-blue-100 text-black overflow-auto"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          type="button"
          className="absolute top-4 right-4 text-2xl"
          onClick={onClose}
        >
          ×
        </button>
        <h2 className="text-xl font-semibold mb-4">Weather Settings</h2>

        {/* Display any search error above the inputs */}
        {searchError && (
          <div className="mb-4 px-4 py-2 text-sm text-red-700 bg-red-100 rounded">
            {searchError}
          </div>
        )}

        {/* Location Search */}
        <div className="mb-4">
          <LocationSearch
            citySearch={localCity}
            onCitySearchChange={setLocalCity}
            onSearch={handleSearchClick}
          />
        </div>

        {/* Unit Toggle */}
        <UnitToggle
          unit={settings.unit}
          onUnitChange={(u) => update({ unit: u })}
        />

        {/* Mode Toggle
        <ModeToggle
          mode={settings.mode}
          onModeChange={(m) => update({ mode: m })}
        /> */}

        {/* Animations Toggle */}
        <AnimationToggle
          animations={settings.animations}
          onAnimationsChange={(anim) =>
            update({
              animations: anim,
              bgStyle: anim ? 'animated' : 'solid',
            })
          }
        />

        {/* Background Style & Pattern Settings */}
        {!settings.animations && (
          <>
            <div className="mb-4">
              <label className="block mb-1 font-medium">Background Style</label>
              <div className="flex gap-4 mb-2">
                {(['solid', 'gradient'] as const).map((bs) => (
                  <label key={bs} className="inline-flex items-center">
                    <input
                      type="radio"
                      name="bgStyle"
                      value={bs}
                      checked={settings.bgStyle === bs}
                      onChange={() => update({ bgStyle: bs })}
                      className="mr-2"
                    />
                    {bs.charAt(0).toUpperCase() + bs.slice(1)}
                  </label>
                ))}
              </div>
            </div>

            {settings.bgStyle === 'solid' ? (
              <SolidSettings
                solidColor={settings.solidColor}
                onSolidColorChange={(c) => update({ solidColor: c })}
                overlayType={settings.overlayType}
                onOverlayTypeChange={(o) => update({ overlayType: o })}
                overlaySettings={settings.overlaySettings}
                onOverlaySettingsChange={(upd) =>
                  update({
                    overlaySettings: { ...settings.overlaySettings, ...upd },
                  })
                }
              />
            ) : (
              <GradientSettings
                gradientStart={settings.gradientStart}
                gradientEnd={settings.gradientEnd}
                blendType={settings.blendType}
                onGradientStartChange={(c) => update({ gradientStart: c })}
                onGradientEndChange={(c) => update({ gradientEnd: c })}
                onBlendTypeChange={(b) => update({ blendType: b })}
              />
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default SettingsModal;
