/* eslint-disable @typescript-eslint/no-explicit-any */
// src/services/holidayService.ts

import { CalendarEvent } from '../types/CalendarEvent';

// In-memory cache: key = `${countryCode}-${year}`
const holidayCache: Record<string, CalendarEvent[]> = {};

/**
 * Fetches holidays for a given country and year.
 * Caches results in-memory and optionally in localStorage.
 * Logs to console when an API call is made.
 * @param countryCode ISO-2 country code
 * @param year four-digit year
 */
export async function fetchHolidays(
  countryCode: string,
  year: number
): Promise<CalendarEvent[]> {
  const cacheKey = `${countryCode}-${year}`;

  // Return from in-memory cache
  if (holidayCache[cacheKey]) {
    console.log(`[HolidayService] Returning cached holidays for ${cacheKey}`);
    return holidayCache[cacheKey];
  }

  // Attempt to read from localStorage
  try {
    const stored = localStorage.getItem(`holidays-${cacheKey}`);
    if (stored) {
      console.log(
        `[HolidayService] Returning localStorage holidays for ${cacheKey}`
      );
      const parsed: CalendarEvent[] = JSON.parse(stored).map((item: any) => ({
        ...item,
        start: new Date(item.start),
        end: item.end ? new Date(item.end) : undefined,
      }));
      holidayCache[cacheKey] = parsed;
      return parsed;
    }
  } catch (err) {
    console.warn(`[HolidayService] localStorage error for ${cacheKey}:`, err);
  }

  // Fetch from backend proxy
  console.log(`[HolidayService] Fetching holidays from API for ${cacheKey}`);
  const response = await fetch(
    `/api/holidays?country=${encodeURIComponent(countryCode)}&year=${year}`
  );
  if (!response.ok) {
    const errorText = await response.text();
    console.error(
      `[HolidayService] API error for ${cacheKey}:`,
      response.status,
      errorText
    );
    throw new Error(`Failed to fetch holidays: ${response.statusText}`);
  }
  const items: any[] = await response.json();

  // Normalize into CalendarEvent[]
  const holidays: CalendarEvent[] = items.map((event) => ({
    id: event.id,
    summary: event.summary,
    start: event.start.date
      ? new Date(event.start.date)
      : new Date(event.start.dateTime),
    end: event.end?.date
      ? new Date(event.end.date)
      : event.end?.dateTime
      ? new Date(event.end.dateTime)
      : undefined,
    isHoliday: true,
  }));

  // Cache in memory
  holidayCache[cacheKey] = holidays;

  // Store in localStorage until year-end
  try {
    localStorage.setItem(`holidays-${cacheKey}`, JSON.stringify(holidays));
  } catch (err) {
    console.warn(
      `[HolidayService] Failed to store in localStorage for ${cacheKey}:`,
      err
    );
  }

  return holidays;
}
