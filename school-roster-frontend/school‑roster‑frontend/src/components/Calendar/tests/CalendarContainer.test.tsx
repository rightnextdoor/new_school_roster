// src/components/Calendar/tests/CalendarContainer.test.tsx

import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import CalendarContainer from '../CalendarContainer';
import * as useCalendarHook from '../../../hooks/useCalendar';
import * as useHolidaysHook from '../../../hooks/useHolidays';
import { ViewType } from '../../../types/ViewType';

// Mock the useCalendar hook
jest
  .spyOn(useCalendarHook, 'useCalendar')
  .mockImplementation((initialDate, initialView) => ({
    selectedDate: initialDate,
    view: initialView,
    goToday: jest.fn(),
    goPrev: jest.fn(),
    goNext: jest.fn(),
    setDate: jest.fn(),
    setView: jest.fn(),
  }));

// Mock the useHolidays hook
jest
  .spyOn(useHolidaysHook, 'useHolidays')
  .mockImplementation((userId, initialCountry) => ({
    holidays: [],
    country: initialCountry,
    changeCountry: jest.fn(),
    error: null,
  }));

describe('CalendarContainer', () => {
  const userId = 'test-user';
  const profileCountry = 'US';
  const today = new Date();
  const initialView: ViewType = 'month';

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders toolbar, view, and settings components', () => {
    render(
      <CalendarContainer
        userId={userId}
        profileCountry={profileCountry}
        initialDate={today}
        initialView={initialView}
      />
    );

    // Toolbar elements
    expect(screen.getByText(/Today/i)).toBeInTheDocument();
    const monthButtons = screen.getAllByRole('button', { name: /Month/i });
    expect(monthButtons.length).toBeGreaterThanOrEqual(1);

    // Settings label
    expect(screen.getByText(/Holiday Country/i)).toBeInTheDocument();
    expect(screen.getByText(/Default View/i)).toBeInTheDocument();

    // Calendar view label
    const monthLabel = today.toLocaleString('default', {
      month: 'long',
      year: 'numeric',
    });
    expect(screen.getByText(new RegExp(monthLabel))).toBeInTheDocument();
  });

  it('calls goToday when Today button is clicked', () => {
    const goTodayMock = jest.fn();
    (useCalendarHook.useCalendar as jest.Mock).mockReturnValueOnce({
      selectedDate: today,
      view: initialView,
      goToday: goTodayMock,
      goPrev: jest.fn(),
      goNext: jest.fn(),
      setDate: jest.fn(),
      setView: jest.fn(),
    });

    render(
      <CalendarContainer
        userId={userId}
        profileCountry={profileCountry}
        initialDate={today}
        initialView={initialView}
      />
    );

    fireEvent.click(screen.getByText(/Today/i));
    expect(goTodayMock).toHaveBeenCalled();
  });
});
