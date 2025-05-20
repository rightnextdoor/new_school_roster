// src/components/weather/widget/WeatherCard.tsx
import React from 'react';

interface WeatherCardProps {
  children: React.ReactNode;
  style?: React.CSSProperties;
  className?: string;
}

const WeatherCard: React.FC<WeatherCardProps> = ({
  children,
  style,
  className,
}) => (
  <div
    className={`w-full h-full relative rounded-2xl overflow-hidden shadow-lg ${
      className ?? ''
    }`}
    style={style}
  >
    {children}
  </div>
);

export default WeatherCard;
