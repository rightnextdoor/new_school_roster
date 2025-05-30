// src/hooks/useCalendar.ts

import { useState, useCallback } from 'react';
import { ViewType } from '../types/ViewType';

/**
 * Hook to manage calendar date and view state.
 * @param initialDate - the initial selected date
 * @param initialView - the initial calendar view
 */
export function useCalendar(initialDate: Date, initialView: ViewType) {
  // Selected date in the calendar
  const [selectedDate, setSelectedDate] = useState<Date>(initialDate);
  // Current view (day, week, month, year)
  const [view, setView] = useState<ViewType>(initialView);

  // Jump to today
  const goToday = useCallback(() => {
    setSelectedDate(new Date());
  }, []);

  // Go to previous period
  const goPrev = useCallback(() => {
    setSelectedDate((prev) => {
      switch (view) {
        case 'day':
          return new Date(
            prev.getFullYear(),
            prev.getMonth(),
            prev.getDate() - 1
          );
        case 'week':
          return new Date(
            prev.getFullYear(),
            prev.getMonth(),
            prev.getDate() - 7
          );
        case 'month':
          return new Date(
            prev.getFullYear(),
            prev.getMonth() - 1,
            prev.getDate()
          );
        case 'year':
          return new Date(
            prev.getFullYear() - 1,
            prev.getMonth(),
            prev.getDate()
          );
      }
    });
  }, [view]);

  // Go to next period
  const goNext = useCallback(() => {
    setSelectedDate((prev) => {
      switch (view) {
        case 'day':
          return new Date(
            prev.getFullYear(),
            prev.getMonth(),
            prev.getDate() + 1
          );
        case 'week':
          return new Date(
            prev.getFullYear(),
            prev.getMonth(),
            prev.getDate() + 7
          );
        case 'month':
          return new Date(
            prev.getFullYear(),
            prev.getMonth() + 1,
            prev.getDate()
          );
        case 'year':
          return new Date(
            prev.getFullYear() + 1,
            prev.getMonth(),
            prev.getDate()
          );
      }
    });
  }, [view]);

  // Set arbitrary date
  const setDate = useCallback((date: Date) => {
    setSelectedDate(date);
  }, []);

  // Change view
  const setCalendarView = useCallback((newView: ViewType) => {
    setView(newView);
  }, []);

  return {
    selectedDate,
    view,
    goToday,
    goPrev,
    goNext,
    setDate,
    setView: setCalendarView,
  };
}
