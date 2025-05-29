/* eslint-disable @typescript-eslint/no-unused-expressions */
/* eslint-disable @typescript-eslint/no-explicit-any */
// src/hooks/useWeather.ts

import { useState, useEffect, useRef, useCallback } from 'react';
import type { WeatherResponse } from '../types/weather';

export interface UseWeatherResult {
  city: string | null;
  data: WeatherResponse | null;
  error: string | null;
  refetch: () => void;
}

export function useWeather(
  searchQuery: string | undefined,
  userId: string,
  fallbackCity = 'London'
): UseWeatherResult {
  const [city, setCity] = useState<string | null>(null);
  const [data, setData] = useState<WeatherResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const timerRef = useRef<number | null>(null);
  const abortRef = useRef<AbortController | null>(null);
  const didMount = useRef(false);
  const prevSearch = useRef<string | undefined>(searchQuery);

  const storageKey = `weatherData_${userId}`;
  const TTL = 10 * 60 * 1000;

  const fetchWeather = useCallback(async () => {
    abortRef.current?.abort();
    const ctrl = new AbortController();
    abortRef.current = ctrl;

    const storedCity = localStorage.getItem('weatherCity');
    const target = searchQuery?.trim() || storedCity || fallbackCity;

    console.log(`[useWeather] ⏳ fetching for "${target}"`);
    try {
      setError(null);

      console.log(`[useWeather] ▶️ GET /api/weather?q=${target}`);
      const locRes = await fetch(
        `/api/weather?q=${encodeURIComponent(target)}&units=metric`,
        { signal: ctrl.signal }
      );
      if (!locRes.ok) throw new Error(`Lookup failed ${locRes.status}`);
      const {
        coord: { lat, lon },
        name,
      } = await locRes.json();
      const finalCity = name || target;
      console.log('[useWeather] ✅ location:', finalCity);

      console.log(
        `[useWeather] ▶️ GET /api/weather/forecast?lat=${lat}&lon=${lon}`
      );
      const fcRes = await fetch(
        `/api/weather/forecast?lat=${lat}&lon=${lon}&units=metric`,
        { signal: ctrl.signal }
      );
      if (!fcRes.ok) throw new Error(`Forecast failed ${fcRes.status}`);
      const fcJson = (await fcRes.json()) as WeatherResponse;
      console.log('[useWeather] ✅ forecast:', fcJson);

      setCity(finalCity);
      setData(fcJson);
      localStorage.setItem('weatherCity', finalCity);
      localStorage.setItem(
        storageKey,
        JSON.stringify({ timestamp: Date.now(), city: finalCity, data: fcJson })
      );
    } catch (e: any) {
      if (e.name !== 'AbortError') {
        console.error('[useWeather] ❌', e);
        setError(e.message);
      }
    } finally {
      if (timerRef.current) clearTimeout(timerRef.current);
      timerRef.current = window.setTimeout(fetchWeather, TTL);
    }
  }, [searchQuery, fallbackCity, storageKey]);

  // 1) Mount: hydrate from cache or fetch once
  useEffect(() => {
    const raw = localStorage.getItem(storageKey);
    if (raw) {
      try {
        const { timestamp, city: c, data: d } = JSON.parse(raw);
        if (Date.now() - timestamp < TTL) {
          setCity(c);
          setData(d);
          const delay = TTL - (Date.now() - timestamp);
          timerRef.current = window.setTimeout(fetchWeather, delay);
          didMount.current = true;
          prevSearch.current = searchQuery;
          return;
        }
      } catch {
        // bad cache, fall through
      }
    }
    fetchWeather();
    didMount.current = true;
    prevSearch.current = searchQuery;

    return () => {
      abortRef.current?.abort();
      timerRef.current && clearTimeout(timerRef.current);
    };
  }, []); // only once

  // 2) On searchQuery **changes** (not on mount)
  useEffect(() => {
    if (
      didMount.current &&
      searchQuery !== undefined &&
      prevSearch.current !== searchQuery
    ) {
      fetchWeather();
      prevSearch.current = searchQuery;
    }
  }, [searchQuery, fetchWeather]);

  return { city, data, error, refetch: fetchWeather };
}
