// src/utils/gradientBackground.ts
import { Settings } from '../types';

/**
 * Builds CSS properties for a gradient background.
 */
export function buildGradientStyle(s: Settings): React.CSSProperties {
  let backgroundImage = '';
  switch (s.blendType) {
    case 'linear':
      backgroundImage = `linear-gradient(90deg, ${s.gradientStart}, ${s.gradientEnd})`;
      break;
    case 'radial':
      backgroundImage = `radial-gradient(circle, ${s.gradientStart}, ${s.gradientEnd})`;
      break;
    case 'conic':
      backgroundImage = `conic-gradient(${s.gradientStart}, ${s.gradientEnd})`;
      break;
  }

  return {
    backgroundImage,
    backgroundRepeat: 'no-repeat',
  };
}
