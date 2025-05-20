// src/types/index.ts

// Unit of temperature
export type Unit = '°F' | '°C';

// Display mode
export type Mode = 'day' | 'weekly';

// Background style
export type BgStyle = 'solid' | 'gradient' | 'animated';

// Gradient blend types
export type BlendType = 'linear' | 'radial' | 'conic';

// Overlay pattern types
export type OverlayType = 'none' | 'waves' | 'stripes' | 'circles' | 'blobs';

// Overlay orientation for waves
export type Orientation = 'horizontal' | 'vertical';

// Settings for pattern overlays
export interface OverlaySettings {
  height: number;
  frequency: number;
  orientation: Orientation;
  angle: number;
  thickness: number;
  size: number;
  count: number;
  sizeVariance: number;
}

// Main weather widget settings schema
export interface Settings {
  citySearch: string;
  unit: Unit;
  mode: Mode;
  animations: boolean;
  bgStyle: BgStyle;
  solidColor: string;
  gradientStart: string;
  gradientEnd: string;
  blendType: BlendType;
  overlayType: OverlayType;
  overlaySettings: OverlaySettings;
}
