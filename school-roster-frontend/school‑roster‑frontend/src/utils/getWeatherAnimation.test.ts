// src/utils/getWeatherAnimation.test.ts

import { getWeatherAnimation } from './getWeatherAnimation';
import clearDay from '../assets/lottie/clear-day.json';
import rain from '../assets/lottie/rain.json';
import heavyRain from '../assets/lottie/heavy-rain.json';
import thunderstorm from '../assets/lottie/thunderstorm.json';
import snow from '../assets/lottie/snow.json';
import heavySnow from '../assets/lottie/heavy-snow.json';

describe('getWeatherAnimation', () => {
  it('returns heavySnow for "heavy snow" description', () => {
    expect(
      getWeatherAnimation({ main: 'Snow', description: 'heavy snow' })
    ).toEqual(heavySnow);
  });

  it('returns snow for "light snow" description', () => {
    expect(
      getWeatherAnimation({ main: 'Snow', description: 'light snow' })
    ).toEqual(snow);
  });

  it('returns heavyRain for "heavy-rain" normalized', () => {
    expect(
      getWeatherAnimation({ main: 'Rain', description: 'heavy-rain' })
    ).toEqual(heavyRain);
  });

  it('returns rain for "moderate rain" description', () => {
    expect(
      getWeatherAnimation({ main: 'Rain', description: 'moderate rain' })
    ).toEqual(rain);
  });

  it('returns thunderstorm for thunderstorm main', () => {
    expect(
      getWeatherAnimation({ main: 'Thunderstorm', description: 'thunderstorm' })
    ).toEqual(thunderstorm);
  });

  it('defaults to clearDay for unknown description and main', () => {
    expect(
      getWeatherAnimation({ main: 'Alien', description: 'solar flare' })
    ).toEqual(clearDay);
  });
});
