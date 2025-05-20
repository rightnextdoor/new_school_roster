// src/utils/solidBackground.ts
import { Settings } from '../types';

/**
 * Builds CSS properties for a solid-color background
 * with optional pattern overlay (waves, stripes, circles, blobs).
 */
export function buildSolidStyle(s: Settings): React.CSSProperties {
  const style: React.CSSProperties = {
    backgroundColor: s.solidColor,
  };

  if (s.overlayType !== 'none') {
    const o = s.overlaySettings;
    let overlay = '';

    switch (s.overlayType) {
      case 'waves': {
        const angle = o.orientation === 'horizontal' ? '0deg' : '90deg';
        overlay = `repeating-linear-gradient(${angle},
          rgba(255,255,255,0.3) 0,
          rgba(255,255,255,0.3) ${o.height}px,
          transparent ${o.height}px,
          transparent ${o.height + o.frequency}px
        )`;
        break;
      }

      case 'stripes':
        overlay = `repeating-linear-gradient(${o.angle}deg,
          rgba(255,255,255,0.3) 0,
          rgba(255,255,255,0.3) ${o.thickness}px,
          transparent ${o.thickness}px,
          transparent ${o.thickness * 2}px
        )`;
        break;

      case 'circles':
        overlay = `radial-gradient(circle,
          rgba(255,255,255,0.3) ${o.size}px,
          transparent ${o.size}px
        )`;
        style.backgroundSize = `${o.size * 2}px ${o.size * 2}px`;
        break;

      case 'blobs':
        overlay = `radial-gradient(circle,
          rgba(255,255,255,0.3) ${o.sizeVariance}px,
          transparent ${o.sizeVariance}px
        )`;
        style.backgroundSize = `${100 / o.count}% ${100 / o.count}%`;
        break;
    }

    style.backgroundImage = overlay.trim();
    style.backgroundRepeat = 'repeat';
  }

  return style;
}
