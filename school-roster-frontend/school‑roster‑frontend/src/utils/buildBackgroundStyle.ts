// src/utils/buildBackgroundStyle.ts
import { Settings } from '../types';
import { buildSolidStyle } from './solidBackground';
import { buildGradientStyle } from './gradientBackground';

/**
 * Chooses between solid (with pattern), gradient, or animations override.
 */
export function buildBackgroundStyle(s: Settings): React.CSSProperties {
  // If animations are enabled, we render the AnimationBackground instead of a CSS style
  if (s.animations) {
    return {};
  }

  // Otherwise return either the solid (with pattern) or gradient style
  return s.bgStyle === 'solid' ? buildSolidStyle(s) : buildGradientStyle(s);
}
