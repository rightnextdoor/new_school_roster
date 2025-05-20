export const WEATHER_API = {
  BASE_URL: 'https://api.openweathermap.org/data/2.5',
  KEY: import.meta.env.VITE_WEATHER_KEY, // define in .env.local
};
export const DEFAULTS = {
  UNIT: 'metric', // 'metric' or 'imperial'
  LANGUAGE: 'en',
};
