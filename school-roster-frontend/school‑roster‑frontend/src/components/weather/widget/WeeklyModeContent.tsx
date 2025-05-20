// src/components/weather/widget/WeeklyModeContent.tsx
import React from 'react';
import type { HourlyForecast, DailyForecast } from '../../../types/weather';
import DetailsGrid from './DetailsGrid';

export interface WeeklyModeContentProps {
  unit: '°F' | '°C';
  color: string;
  hourly: HourlyForecast[];
  daily: DailyForecast[];
}

const WeeklyModeContent: React.FC<WeeklyModeContentProps> = ({
  unit,
  color,
  hourly,
  daily,
}) => {
  // Show only next 24 hours and next 7 days
  const displayHourly = hourly.slice(0, 24);
  const displayDaily = daily.slice(0, 7);

  // Convert API hourly data into UI format
  const hourlyData = displayHourly.map((h) => {
    const date = new Date(h.dt * 1000);
    const time = date.toLocaleTimeString([], {
      hour: 'numeric',
      minute: '2-digit',
    });
    const cTemp = h.temp;
    const fTemp = Math.round((cTemp * 9) / 5 + 32);
    const pop = Math.round((h.pop ?? 0) * 100);
    const iconCode = h.weather[0]?.icon;
    const iconUrl = `https://openweathermap.org/img/wn/${iconCode}@2x.png`;
    return { time, iconUrl, tempF: fTemp, tempC: cTemp, pop };
  });

  // Convert API daily data into UI format
  const dailyData = displayDaily.map((d) => {
    const date = new Date(d.dt * 1000);
    const name = date.toLocaleDateString([], { weekday: 'short' });
    const cHi = d.temp.max;
    const cLo = d.temp.min;
    const fHi = Math.round((cHi * 9) / 5 + 32);
    const fLo = Math.round((cLo * 9) / 5 + 32);
    const pop = Math.round((d.pop ?? 0) * 100);
    const iconCode = d.weather[0]?.icon;
    const iconUrl = `https://openweathermap.org/img/wn/${iconCode}@2x.png`;
    return { name, iconUrl, hiF: fHi, loF: fLo, hiC: cHi, loC: cLo, pop };
  });

  return (
    <div className="overflow-y-auto flex-1 p-4 space-y-6">
      {/* Hourly */}
      <div className="bg-white/10 rounded-lg p-4">
        <h3 className="text-lg font-semibold mb-3" style={{ color }}>
          Hourly
        </h3>
        <div className="flex overflow-x-auto gap-6">
          {hourlyData.map((h, idx) => (
            <div key={idx} className="flex flex-col items-center min-w-[80px]">
              <p className="mb-1" style={{ color }}>
                {h.time}
              </p>
              <img
                src={h.iconUrl}
                alt="weather icon"
                className="w-8 h-8 mb-1"
                style={{ filter: `drop-shadow(0 0 2px ${color})` }}
              />
              <p className="font-bold mb-1" style={{ color }}>
                {unit === '°F' ? `${h.tempF}${unit}` : `${h.tempC}${unit}`}
              </p>
              <p className="text-sm opacity-80" style={{ color }}>
                {h.pop}%
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* 7-Day Forecast */}
      <div className="bg-white/10 rounded-lg p-4">
        <h3 className="text-lg font-semibold mb-3" style={{ color }}>
          7-Day Forecast
        </h3>
        <div className="flex flex-col divide-y divide-white/30">
          {dailyData.map((d, idx) => (
            <div key={idx} className="flex justify-between items-center py-2">
              <p style={{ color }}>{d.name}</p>
              <div className="flex items-center gap-2">
                <img
                  src={d.iconUrl}
                  alt="weather icon"
                  className="w-6 h-6"
                  style={{ filter: `drop-shadow(0 0 2px ${color})` }}
                />
                <p style={{ color }}>
                  {unit === '°F'
                    ? `${d.hiF}° / ${d.loF}°`
                    : `${d.hiC}° / ${d.loC}°`}
                </p>
              </div>
              <p className="text-sm opacity-80" style={{ color }}>
                {d.pop}%
              </p>
            </div>
          ))}
        </div>
      </div>

      {/* Details Grid */}
      <DetailsGrid color={color} />
    </div>
  );
};

export default WeeklyModeContent;
