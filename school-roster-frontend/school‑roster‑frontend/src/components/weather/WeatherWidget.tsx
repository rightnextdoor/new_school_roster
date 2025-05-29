// src/components/weather/WeatherWidget.tsx
import React, { useState, useEffect } from 'react';
import SettingsModal, { Settings } from './SettingsModal';
import WeatherCard from './widget/WeatherCard';
import SettingsButton from './widget/SettingsButton';
import AnimationBackground from './background/AnimationBackground';
import DayModeContent from './widget/DayModeContent';
// import WeeklyModeContent from './widget/WeeklyModeContent';
import { buildBackgroundStyle } from '../../utils/buildBackgroundStyle';
import { useWeather } from '../../hooks/useWeather';
import { animationContrastMap } from '../../utils/animationContrast';

// helper to turn hex into RGB tuple
function hexToRgb(hex: string): [number, number, number] {
  const h = hex.replace('#', '');
  const i = parseInt(h, 16);
  return [(i >> 16) & 255, (i >> 8) & 255, i & 255];
}

const defaultSettings: Settings = {
  citySearch: '',
  unit: '°C',
  mode: 'day',
  animations: true,
  bgStyle: 'solid',
  solidColor: '#3b82f6',
  gradientStart: '#3b82f6',
  gradientEnd: '#9333ea',
  blendType: 'linear',
  overlayType: 'none',
  overlaySettings: {
    height: 20,
    frequency: 20,
    orientation: 'horizontal',
    angle: 45,
    thickness: 10,
    size: 20,
    count: 5,
    sizeVariance: 20,
  },
};

export interface WeatherWidgetProps {
  userId: string;
  defaultCity?: string; // will use London if not provided
}

export default function WeatherWidget({
  userId,
  defaultCity = 'London',
}: WeatherWidgetProps) {
  const settingsKey = `weatherSettings_${userId}`;

  // load or initialize settings (seed citySearch with defaultCity)
  const [settings, setSettings] = useState<Settings>(() => {
    try {
      const stored = localStorage.getItem(settingsKey);
      return stored
        ? JSON.parse(stored)
        : { ...defaultSettings, citySearch: defaultCity };
    } catch {
      return { ...defaultSettings, citySearch: defaultCity };
    }
  });

  const [showSettings, setShowSettings] = useState(false);
  const [searchError, setSearchError] = useState<string | null>(null);

  const { city, data, error } = useWeather(
    settings.citySearch,
    userId,
    defaultCity
  );

  // persist settings
  useEffect(() => {
    localStorage.setItem(settingsKey, JSON.stringify(settings));
  }, [settings, settingsKey]);

  // lock scroll when modal open
  useEffect(() => {
    document.body.style.overflow = showSettings ? 'hidden' : 'auto';
  }, [showSettings]);

  // handle API errors (only for modal)
  useEffect(() => {
    if (error) {
      setSearchError(`Unable to fetch weather for "${settings.citySearch}".`);
    } else if (data) {
      setSearchError(null);
    }
  }, [error, data, settings.citySearch]);

  // background style
  const staticStyle = buildBackgroundStyle(settings);

  // figure out which Lottie condition we’re showing
  const currentCondition = data?.current?.weather?.[0]?.main ?? '';

  // look up our dark/light map (default to light)
  const animationEntry = animationContrastMap[currentCondition] ?? {
    mode: 'light',
  };

  // contrast for text/icons
  const contrastColor = settings.animations
    ? animationEntry.mode === 'dark'
      ? '#fff'
      : '#000'
    : (() => {
        if (settings.bgStyle === 'solid') {
          const [r, g, b] = hexToRgb(settings.solidColor);
          const lum = 0.299 * r + 0.587 * g + 0.114 * b;
          return lum < 128 ? '#fff' : '#000';
        }
        return '#fff';
      })();

  // alert color
  const alertColorFinal = settings.animations
    ? animationEntry.alertColor ?? '#DC2626'
    : '#DC2626';

  // trigger search (settings.citySearch already updated by modal)
  const handleSearch = () => {
    setSearchError(null);
  };

  return (
    <>
      {showSettings && (
        <SettingsModal
          isOpen={showSettings}
          settings={settings}
          onSettingsChange={setSettings}
          onSearch={handleSearch}
          searchError={searchError}
          onClose={() => setShowSettings(false)}
        />
      )}

      <WeatherCard className="relative overflow-hidden">
        {/* background */}
        {settings.animations && data && data.current ? (
          <AnimationBackground
            weatherMain={data.current.weather[0].main}
            weatherDescription={data.current.weather[0].description}
          />
        ) : (
          <div className="absolute inset-0" style={staticStyle} />
        )}

        {/* content */}
        <div className="relative z-10 w-full h-full flex flex-col">
          <SettingsButton
            color={contrastColor}
            onClick={() => setShowSettings(true)}
          />

          <div className="flex-1 flex flex-col">
            {/* loading state */}
            {!data && !error && (
              <div className="flex items-center justify-center h-full">
                <p>Loading weather…</p>
              </div>
            )}

            {/* only render when data is ready */}
            {data && data.current && (
              <DayModeContent
                unit={settings.unit}
                color={contrastColor}
                alertColor={alertColorFinal}
                city={city ?? defaultCity}
                timezone={data.timezone} // ← pass the API’s timezone string
                description={data.current.weather[0]?.description ?? ''}
                sunriseMs={data.current.sunrise * 1000}
                sunsetMs={data.current.sunset * 1000}
                tempF={Math.round((data.current.temp * 9) / 5 + 32)}
                tempC={data.current.temp}
                iconCode={data.current.weather[0].icon}
                alerts={data.alerts}
              />
            )}
          </div>
        </div>
      </WeatherCard>
    </>
  );
}
