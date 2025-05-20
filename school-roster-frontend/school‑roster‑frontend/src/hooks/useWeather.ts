import { useState, useEffect, useRef, useCallback } from 'react';
import type { WeatherResponse } from '../types/weather';

export interface UseWeatherResult {
  city: string | null;
  data: WeatherResponse | null;
  error: string | null;
  refetch: () => void;
}

/**
 * Fetches One-Call weather for the given city/ZIP:
 * 1) Hits /api/weather?q=… to resolve lat/lon & city name
 * 2) Hits /api/weather/forecast?lat=…&lon=… to pull full One-Call payload
 * Re-runs immediately on searchQuery change, and every 10 minutes thereafter.
 */
export function useWeather(searchQuery?: string): UseWeatherResult {
  const [city, setCity] = useState<string | null>(null);
  const [data, setData] = useState<WeatherResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const timerRef = useRef<number | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  const fetchWeather = useCallback(async () => {
    // abort any existing request
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    // pick target: explicit search → last stored → fallback
    const stored = localStorage.getItem('weatherCity');
    const defaultCity = 'London';
    const target = searchQuery?.trim() || stored || defaultCity;

    try {
      setError(null);

      // 1) lookup coords & city
      const locRes = await fetch(
        `/api/weather?q=${encodeURIComponent(target)}&units=metric`,
        { signal: controller.signal }
      );
      if (!locRes.ok) throw new Error(`Lookup failed ${locRes.status}`);
      const locJson = await locRes.json();
      const {
        coord: { lat, lon },
        name,
      } = locJson;

      // 2) fetch One-Call full payload
      const fcRes = await fetch(
        `/api/weather/forecast?lat=${lat}&lon=${lon}&units=metric`,
        { signal: controller.signal }
      );
      if (!fcRes.ok) throw new Error(`Forecast failed ${fcRes.status}`);
      const fcJson = (await fcRes.json()) as WeatherResponse;

      // store city and data
      setCity(name || target);
      setData(fcJson);
      localStorage.setItem('weatherCity', name || target);
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (e: any) {
      if (e.name !== 'AbortError') {
        setError(e.message);
      }
    } finally {
      // schedule next fetch in 10 minutes
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = window.setTimeout(fetchWeather, 10 * 60 * 1000);
    }
  }, [searchQuery]);

  useEffect(() => {
    fetchWeather();
    return () => {
      abortRef.current?.abort();
      if (timerRef.current) clearTimeout(timerRef.current);
    };
  }, [fetchWeather]);

  return { city, data, error, refetch: fetchWeather };
}
