// src/types/CalendarEvent.ts

/**
 * Represents an event shown on the calendar.
 * `isHoliday` flags pulled-in public holidays.
 */
export interface CalendarEvent {
  id: string;
  summary: string;
  start: Date;
  end?: Date;
  isHoliday?: boolean;
}
