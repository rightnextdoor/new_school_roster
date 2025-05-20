// src/components/weather/widget/SunMoonAnimation.tsx
import React, { useRef, useState, useEffect } from 'react';

export interface SunMoonAnimationProps {
  /** Sunrise time in ms since epoch */
  sunriseMs: number;
  /** Sunset time in ms since epoch */
  sunsetMs: number;
  /** Color for the arc and sun/moon */
  color: string;
}

const SunMoonAnimation: React.FC<SunMoonAnimationProps> = ({
  sunriseMs,
  sunsetMs,
  color,
}) => {
  const svgRef = useRef<SVGSVGElement>(null);
  const [arcLength, setArcLength] = useState(0);

  // Measure the foreground arc length once
  useEffect(() => {
    if (svgRef.current) {
      const fg = svgRef.current.querySelector('path.foreground');
      if (fg) setArcLength((fg as SVGPathElement).getTotalLength());
    }
  }, []);

  // Compute current fraction of sun's path
  const now = Date.now();
  let fraction = 0;
  if (now < sunriseMs) fraction = 0;
  else if (now > sunsetMs) fraction = 1;
  else fraction = (now - sunriseMs) / (sunsetMs - sunriseMs);
  fraction = Math.max(0, Math.min(1, fraction));

  const dashOffset = arcLength * (1 - fraction);

  return (
    <svg
      ref={svgRef}
      width="200"
      height="100"
      viewBox="0 0 200 100"
      className="mx-auto"
    >
      {/* Background arc */}
      <path
        d="M10,100 A90,90 0 0,1 190,100"
        fill="none"
        stroke="rgba(255,255,255,0.3)"
        strokeWidth="4"
      />

      {/* Foreground arc */}
      <path
        className="foreground"
        d="M10,100 A90,90 0 0,1 190,100"
        fill="none"
        stroke={color}
        strokeWidth="4"
        strokeDasharray={arcLength}
        strokeDashoffset={dashOffset}
      />

      {/* Sun/Moon indicator */}
      <circle
        r="6"
        fill={color}
        cx={10 + 180 * fraction}
        cy={100 - Math.sin(Math.PI * fraction) * 90}
      />
    </svg>
  );
};

export default SunMoonAnimation;
