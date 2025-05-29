// src/components/weather/widget/DayModeContent.tsx
import React from 'react';
import SunMoonAnimation from './SunMoonAnimation';

export interface WeatherAlert {
  event: string;
  description?: string;
}

export interface DayModeContentProps {
  unit: '°F' | '°C';
  color: string;
  alertColor: string;
  city: string;
  timezone: string;
  description: string;
  sunriseMs: number;
  sunsetMs: number;
  tempF: number;
  tempC: number;
  iconCode: string;
  alerts?: WeatherAlert[];
}

const DayModeContent: React.FC<DayModeContentProps> = ({
  unit,
  color,
  alertColor,
  city,
  timezone,
  description,
  sunriseMs,
  sunsetMs,
  tempF,
  tempC,
  iconCode,
  alerts,
}) => {
  // temperature display
  const display = unit === '°F' ? `${tempF}°F` : `${Math.round(tempC)}°C`;

  // now in city’s timezone
  const now = new Date();
  const dateStr = now.toLocaleDateString([], {
    timeZone: timezone,
    weekday: 'short',
    month: 'short',
    day: 'numeric',
  });
  const timeStr = now.toLocaleTimeString([], {
    timeZone: timezone,
    hour: '2-digit',
    minute: '2-digit',
  });

  // next sunrise/sunset countdown (unchanged)
  const sunriseDate = new Date(sunriseMs);
  const sunsetDate = new Date(sunsetMs);
  let nextEvent = sunriseDate;
  let label = 'sunrise';
  if (now > sunriseDate) {
    nextEvent = sunsetDate;
    label = 'sunset';
  }
  if (now > nextEvent) {
    const tom = new Date(now);
    tom.setDate(now.getDate() + 1);
    nextEvent = new Date(
      `${tom.toDateString()} ${sunriseDate.toLocaleTimeString([], {
        hour: '2-digit',
        minute: '2-digit',
      })}`
    );
    label = 'sunrise';
  }
  const delta = nextEvent.getTime() - now.getTime();
  const h = Math.floor(delta / (1000 * 60 * 60));
  const m = Math.floor((delta % (1000 * 60 * 60)) / (1000 * 60));
  const nextText = `${h > 0 ? `${h}h ` : ''}${m}m to ${label}`;

  return (
    <div className="relative w-full h-full text-white" style={{ color }}>
      {/* Top-left: temperature & city */}
      <div className="absolute top-4 left-4 text-left">
        <p className="text-5xl font-bold">{display}</p>
        <p className="text-lg opacity-80">{city}</p>
      </div>

      {/* Sun/Moon animation */}
      <div className="absolute top-4 left-1/2 transform -translate-x-1/2 w-48 h-12">
        <SunMoonAnimation
          sunriseMs={sunriseMs}
          sunsetMs={sunsetMs}
          color={color}
        />
      </div>

      {/* Top-right: weather icon */}
      <div className="absolute top-1/2 right-4 transform -translate-y-1/2 pointer-events-none">
        <img
          src={`https://openweathermap.org/img/wn/${iconCode}@4x.png`}
          alt={description}
          className="w-12 h-12"
        />
      </div>

      {/* Alerts */}
      {alerts && alerts.length > 0 && (
        <div className="absolute inset-0 flex items-center justify-center pointer-events-none">
          <p
            style={{ color: alertColor }}
            className="bg-black/50 px-2 py-1 rounded"
          >
            ⚠️ {alerts[0].event}
          </p>
        </div>
      )}

      {/* Bottom-left: description */}
      <div className="absolute bottom-4 left-4" style={{ color }}>
        <p className="text-lg">{description}</p>
      </div>

      {/* Bottom-center: next event countdown */}
      <div
        className="absolute bottom-4 left-1/2 transform -translate-x-1/2"
        style={{ color }}
      >
        <p className="text-sm font-medium">{nextText}</p>
      </div>

      {/* Bottom-right: date & local time */}
      <div className="absolute bottom-4 right-4 text-right" style={{ color }}>
        <p className="text-sm opacity-80">{dateStr}</p>
        <p className="text-3xl font-semibold leading-none">{timeStr}</p>
      </div>
    </div>
  );
};

export default DayModeContent;
