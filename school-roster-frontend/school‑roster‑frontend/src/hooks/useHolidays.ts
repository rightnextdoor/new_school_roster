// src/hooks/useHolidays.ts

import { useState, useEffect } from 'react';
import { CalendarEvent } from '../types/CalendarEvent';
import { fetchHolidays } from '../services/holidayService';

/**
 * Hook to fetch and cache holidays for a given country and year.
 * @param initialCountry - ISO-2 code from user profile or localStorage
 * @param initialYear - year to load holidays for
 */
export function useHolidays(
  userId: string,
  initialCountry: string,
  initialYear: number
) {
  const storageKey = `calendar-country-${userId}`;
  const [country, setCountry] = useState<string>(() => {
    return localStorage.getItem(storageKey) || initialCountry;
  });
  const [holidays, setHolidays] = useState<CalendarEvent[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let isMounted = true;
    setError(null);

    fetchHolidays(country, initialYear)
      .then((list) => {
        if (isMounted) {
          setHolidays(list);
        }
      })
      .catch((err) => {
        console.error('[useHolidays]', err);
        if (isMounted) setError(err.message);
      });

    return () => {
      isMounted = false;
    };
  }, [country, initialYear, userId]);

  const changeCountry = (newCountry: string) => {
    localStorage.setItem(storageKey, newCountry);
    setCountry(newCountry);
  };

  return { holidays, country, changeCountry, error };
}
