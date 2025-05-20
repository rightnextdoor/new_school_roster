// src/utils/animationContrast.ts

export type ContrastMode = 'light' | 'dark';

export interface AnimationContrastEntry {
  /**
   * If 'dark', we’ll render text/arc/alerts in white;
   * if 'light', in black.
   */
  mode: ContrastMode;

  /**
   * Optional: override just the alert color (e.g. when the bg itself is red).
   * If omitted, alerts stay '#DC2626' (red-500) but will be white on dark bg.
   */
  alertColor?: string;
}

/**
 * Hard-coded mapping of each Lottie condition
 * to whether its overall background is light or dark.
 */
export const animationContrastMap: Record<string, AnimationContrastEntry> = {
  Clear: { mode: 'light' },
  Clouds: { mode: 'light' },
  Drizzle: { mode: 'light' },
  Rain: { mode: 'light' },
  HeavyRain: { mode: 'light' },
  Thunderstorm: { mode: 'light' },
  Snow: { mode: 'light' },
  HeavySnow: { mode: 'light' },
  Sleet: { mode: 'light' },
  Tornado: { mode: 'light' },
  Hurricane: { mode: 'light', alertColor: '#000' /* black alert on red bg */ },
  Fog: { mode: 'light' },
  Windy: { mode: 'light' },
};
