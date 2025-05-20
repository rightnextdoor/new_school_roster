// src/utils/getWeatherAnimation.ts

import { normalizeWeatherString } from './normalizeWeatherString';

import clearDay from '../assets/lottie/clear-day.json';
import clouds from '../assets/lottie/clouds.json';
import lightRain from '../assets/lottie/light-rain.json';
import rain from '../assets/lottie/rain.json';
import heavyRain from '../assets/lottie/heavy-rain.json';
import thunderstorm from '../assets/lottie/thunderstorm.json';
import snow from '../assets/lottie/snow.json';
import heavySnow from '../assets/lottie/heavy-snow.json';
import sleet from '../assets/lottie/sleet.json';
import tornado from '../assets/lottie/tornado.json';
import hurricane from '../assets/lottie/hurricane.json';
import fog from '../assets/lottie/fog.json';
import windy from '../assets/lottie/windy.json';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const animationMap: Record<string, any> = {
  Clear: clearDay,
  Clouds: clouds,
  Drizzle: lightRain,
  Rain: rain,
  HeavyRain: heavyRain,
  Thunderstorm: thunderstorm,
  Snow: snow,
  HeavySnow: heavySnow,
  Sleet: sleet,
  Tornado: tornado,
  Hurricane: hurricane,
  Fog: fog,
  Windy: windy,
};

export interface WeatherInfo {
  main: string;
  description: string;
}

/**
 * Pick the right animation for given weather info.
 */
// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function getWeatherAnimation({ main, description }: WeatherInfo): any {
  const desc = normalizeWeatherString(description);

  // 1) Specific first:
  if (desc.includes('heavy snow')) return animationMap.HeavySnow;
  if (desc.includes('heavy rain')) return animationMap.HeavyRain;

  // 2) Then other keywords:
  if (desc.includes('snow')) return animationMap.Snow;
  if (desc.includes('drizzle')) return animationMap.Drizzle;
  if (desc.includes('rain')) return animationMap.Rain;
  if (desc.includes('thunderstorm')) return animationMap.Thunderstorm;
  if (desc.includes('sleet')) return animationMap.Sleet;
  if (desc.includes('tornado')) return animationMap.Tornado;
  if (desc.includes('hurricane')) return animationMap.Hurricane;
  if (desc.includes('fog')) return animationMap.Fog;
  if (desc.includes('cloud')) return animationMap.Clouds;
  if (desc.includes('wind')) return animationMap.Windy;
  if (desc.includes('clear')) return animationMap.Clear;

  // 3) Fallback to the `main` category (case‐insensitive)
  const normMain = normalizeWeatherString(main);
  const matchKey = Object.keys(animationMap).find(
    (key) => key.toLowerCase() === normMain
  );
  if (matchKey) return animationMap[matchKey];

  // 4) Ultimate default
  return animationMap.Clear;
}
