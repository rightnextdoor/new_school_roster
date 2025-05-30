// src/utils/dateUtils.ts

import {
  startOfWeek as dfStartOfWeek,
  startOfMonth as dfStartOfMonth,
  startOfYear as dfStartOfYear,
  addDays,
  format,
  type Day,
} from 'date-fns';
import { ViewType } from '../types/ViewType';

/**
 * Returns the start of the week for a given date.
 * @param date - any date within the week
 * @param weekStartsOn - 0 (Sunday) through 6 (Saturday)
 */
export function getStartOfWeek(date: Date, weekStartsOn: Day = 0): Date {
  return dfStartOfWeek(date, { weekStartsOn });
}

/**
 * Returns the first day of the month for a given date.
 */
export function getStartOfMonth(date: Date): Date {
  return dfStartOfMonth(date);
}

/**
 * Returns the first day of the year for a given date.
 */
export function getStartOfYear(date: Date): Date {
  return dfStartOfYear(date);
}

/**
 * Given a date and view type, returns an array of dates that should be visible.
 */
export function getVisibleRange(date: Date, view: ViewType): Date[] {
  switch (view) {
    case 'day':
      return [date];

    case 'week': {
      const start = getStartOfWeek(date);
      return Array.from({ length: 7 }, (_, i) => addDays(start, i));
    }

    case 'month': {
      // 6 weeks × 7 days = 42 days grid
      const firstOfMonth = getStartOfMonth(date);
      const start = getStartOfWeek(firstOfMonth);
      return Array.from({ length: 42 }, (_, i) => addDays(start, i));
    }

    case 'year': {
      // first day of each month
      return Array.from(
        { length: 12 },
        (_, i) => new Date(date.getFullYear(), i, 1)
      );
    }
  }
}

/**
 * Formats the calendar header label based on the view.
 */
export function formatLabel(date: Date, view: ViewType): string {
  switch (view) {
    case 'day':
      return format(date, 'MMMM d, yyyy');

    case 'week': {
      const start = getStartOfWeek(date);
      const end = addDays(start, 6);
      return `${format(start, 'MMM d')} - ${format(end, 'MMM d, yyyy')}`;
    }

    case 'month':
      return format(date, 'MMMM yyyy');

    case 'year':
      return format(date, 'yyyy');
  }
}
