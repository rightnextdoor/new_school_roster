import { Request, Response, NextFunction } from 'express';
import countries from 'i18n-iso-countries';
import enLocale from 'i18n-iso-countries/langs/en.json';
import holidayCalendarMap from '../data/holidays.json';

countries.registerLocale(enLocale);

// Build a lookup: ISO-2 code ⇒ Google calendar ID
const calendarIdMap: Record<string, string> = {};
for (const [countryName, calendarId] of Object.entries(holidayCalendarMap)) {
  // getAlpha2Code returns e.g. 'US' for 'United States'
  const code = countries.getAlpha2Code(countryName, 'en');
  if (code) {
    calendarIdMap[code.toUpperCase()] = calendarId;
  }
}

export const getHolidays = async (
  req: Request,
  res: Response,
  next: NextFunction
) => {
  try {
    const raw = String(req.query.country || '').toUpperCase(); // e.g. 'US'
    const year = Number(req.query.year || new Date().getFullYear());
    const key = process.env.GOOGLE_CALENDAR_API_KEY;
    if (!key) {
      return res.status(500).json({ error: 'API key not configured' });
    }

    // lookup our prebuilt map, fall back to the generic pattern if missing
    const calId =
      calendarIdMap[raw] ||
      `${raw.toLowerCase()}.holiday@group.v.calendar.google.com`;

    const encodedCalId = encodeURIComponent(calId);
    const timeMin = `${year}-01-01T00:00:00Z`;
    const timeMax = `${year}-12-31T23:59:59Z`;
    const url =
      `https://www.googleapis.com/calendar/v3/calendars/${encodedCalId}/events` +
      `?key=${encodeURIComponent(key)}` +
      `&timeMin=${encodeURIComponent(timeMin)}` +
      `&timeMax=${encodeURIComponent(timeMax)}`;

    const apiRes = await fetch(url);
    if (!apiRes.ok) {
      const errText = await apiRes.text();
      return res.status(apiRes.status).send(errText);
    }
    const { items } = await apiRes.json();
    return res.json(items);
  } catch (err) {
    next(err);
  }
};
