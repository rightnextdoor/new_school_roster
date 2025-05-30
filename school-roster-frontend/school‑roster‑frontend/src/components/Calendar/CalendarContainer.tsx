// src/components/Calendar/CalendarContainer.tsx

import React, { useState, useEffect } from 'react';
import { useCalendar } from '../../hooks/useCalendar';
import { useHolidays } from '../../hooks/useHolidays';
import { ViewType } from '../../types/ViewType';
import CalendarToolbar from './components/CalendarToolbar';
import CalendarView from './components/CalendarView';
import CalendarSettings from './components/CalendarSettings';
import styles from './styles/calendar.module.css';

interface CalendarContainerProps {
  userId: string;
  profileCountry: string;
  initialDate: Date;
  initialView: ViewType;
  onDateChange?: (date: Date) => void;
  onViewChange?: (view: ViewType) => void;
}

export default function CalendarContainer({
  userId,
  profileCountry,
  initialDate,
  initialView,
  onDateChange,
  onViewChange,
}: CalendarContainerProps) {
  const { selectedDate, view, goToday, goPrev, goNext, setDate, setView } =
    useCalendar(initialDate, initialView);

  const defaultCountry = profileCountry || 'PH';

  const { holidays, changeCountry, error } = useHolidays(
    userId,
    defaultCountry,
    selectedDate.getFullYear()
  );

  const [showSettings, setShowSettings] = useState(false);

  useEffect(() => {
    onDateChange?.(selectedDate);
  }, [selectedDate, onDateChange]);

  useEffect(() => {
    onViewChange?.(view);
  }, [view, onViewChange]);

  return (
    <div className={`${styles.container} flex flex-col h-full relative`}>
      {/* Settings icon */}
      <button
        onClick={() => setShowSettings(true)}
        aria-label="Settings"
        className="absolute top-3 right-3 text-gray-600 hover:text-gray-800"
      >
        ⚙️
      </button>

      {/* Toolbar */}
      <CalendarToolbar
        selectedDate={selectedDate}
        view={view}
        onToday={goToday}
        onPrev={goPrev}
        onNext={goNext}
        onPickDate={setDate}
      />

      {/* Calendar grid */}
      <div className="flex-1 overflow-auto">
        <CalendarView
          date={selectedDate}
          view={view}
          events={holidays}
          selectedDate={selectedDate}
          onPickDate={setDate}
          onPrev={goPrev}
          onNext={goNext}
        />
      </div>

      {/* Error message */}
      {error && (
        <div className="text-red-500 text-sm mt-2 px-2">
          Failed to fetch holidays: {error}
        </div>
      )}

      {/* Settings modal */}
      {showSettings && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-gradient-to-br from-white to-blue-100 rounded-lg shadow-lg w-96 p-4">
            <div className="flex justify-end">
              <button
                onClick={() => setShowSettings(false)}
                aria-label="Close settings"
                className="text-gray-500 hover:text-gray-700"
              >
                ✕
              </button>
            </div>
            <CalendarSettings
              userId={userId}
              profileCountry={profileCountry}
              currentView={view}
              onViewChange={(newView) => {
                setView(newView);
                setShowSettings(false);
              }}
              onCountryChange={(newCountry) => {
                changeCountry(newCountry);
                setShowSettings(false);
              }}
            />
          </div>
        </div>
      )}
    </div>
  );
}
