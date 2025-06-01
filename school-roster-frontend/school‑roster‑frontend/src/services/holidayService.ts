/* eslint-disable @typescript-eslint/no-explicit-any */
// src/services/holidayService.ts

import { CalendarEvent } from '../types/CalendarEvent';

// In-memory cache
const holidayCache: Record<string, CalendarEvent[]> = {};

// Helper: parse a YMD string as **local** midnight
function parseLocalDate(ymd: string): Date {
  const [y, m, d] = ymd.split('-').map(Number);
  return new Date(y, m - 1, d);
}

/**
 * Fetches holidays for a country/year, with caching.
 */
export async function fetchHolidays(
  countryCode: string,
  year: number
): Promise<CalendarEvent[]> {
  const cacheKey = `${countryCode}-${year}`;

  // 1) In-memory
  if (holidayCache[cacheKey]) {
    console.log(`[HolidayService] cached in-memory ${cacheKey}`);
    return holidayCache[cacheKey];
  }

  // 2) localStorage
  try {
    const raw = localStorage.getItem(`holidays-${cacheKey}`);
    if (raw) {
      console.log(`[HolidayService] cached in localStorage ${cacheKey}`);
      const arr: any[] = JSON.parse(raw);
      const parsed: CalendarEvent[] = arr.map((item) => ({
        ...item,
        // item.start was stored as ISO string → slice to YYYY-MM-DD
        start: parseLocalDate(item.start.slice(0, 10)),
        end: item.end ? parseLocalDate(item.end.slice(0, 10)) : undefined,
      }));
      holidayCache[cacheKey] = parsed;
      return parsed;
    }
  } catch (e) {
    console.warn(`[HolidayService] localStorage read failed ${cacheKey}`, e);
  }

  // 3) Fetch from API
  console.log(`[HolidayService] fetching API ${cacheKey}`);
  const res = await fetch(
    `/api/holidays?country=${encodeURIComponent(countryCode)}&year=${year}`
  );
  if (!res.ok) {
    const txt = await res.text();
    console.error(`[HolidayService] API error ${cacheKey}`, res.status, txt);
    throw new Error(res.statusText);
  }
  const items: any[] = await res.json();

  // Normalize all event dates into local Date
  const holidays: CalendarEvent[] = items.map((ev) => {
    // prefer .date (all-day) or fallback to dateTime
    const s = ev.start.date ?? ev.start.dateTime.slice(0, 10);
    const e = ev.end?.date ?? ev.end?.dateTime?.slice(0, 10);
    return {
      id: ev.id,
      summary: ev.summary,
      start: parseLocalDate(s),
      end: e ? parseLocalDate(e) : undefined,
      isHoliday: true,
    };
  });

  // Cache in-memory
  holidayCache[cacheKey] = holidays;

  // Store raw ISO strings in localStorage (so we can re-hydrate via the same slice+parse)
  try {
    localStorage.setItem(
      `holidays-${cacheKey}`,
      JSON.stringify(
        holidays.map((h) => ({
          ...h,
          // toISOString gives "2025-05-18T00:00:00.000Z"
          start: h.start.toISOString(),
          end: h.end?.toISOString(),
        }))
      )
    );
  } catch (e) {
    console.warn(`[HolidayService] localStorage write failed ${cacheKey}`, e);
  }

  return holidays;
}
