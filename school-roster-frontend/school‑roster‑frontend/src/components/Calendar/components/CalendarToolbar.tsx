// src/components/Calendar/components/CalendarToolbar.tsx

import React, { useState, useEffect } from 'react';
import { format } from 'date-fns';
import { ViewType } from '../../../types/ViewType';

interface CalendarToolbarProps {
  selectedDate: Date;
  view: ViewType;
  onToday(): void;
  onPrev(): void;
  onNext(): void;
  onPickDate(date: Date): void;
}

export default function CalendarToolbar({
  selectedDate,
  view,
  onToday,
  onPrev,
  onNext,
  onPickDate,
}: CalendarToolbarProps) {
  const [dateInput, setDateInput] = useState<string>(
    format(selectedDate, 'yyyy-MM-dd')
  );

  // Sync picker when selectedDate changes
  useEffect(() => {
    setDateInput(format(selectedDate, 'yyyy-MM-dd'));
  }, [selectedDate]);

  // Auto-navigate on pick
  const handleDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setDateInput(value);
    // parse as local date to avoid UTC shift:
    const [year, month, day] = value.split('-').map(Number);
    if (!Number.isNaN(year) && !Number.isNaN(month) && !Number.isNaN(day)) {
      onPickDate(new Date(year, month - 1, day));
    }
  };

  // Label per view
  let label: string;
  switch (view) {
    case 'day':
      label = format(selectedDate, 'MMMM d, yyyy');
      break;
    case 'week': {
      const start = new Date(selectedDate);
      start.setDate(start.getDate() - ((start.getDay() + 6) % 7));
      const end = new Date(start);
      end.setDate(start.getDate() + 6);
      label = `${format(start, 'MMM d')} – ${format(end, 'MMM d, yyyy')}`;
      break;
    }
    case 'month':
      label = format(selectedDate, 'MMMM yyyy');
      break;
    case 'year':
      label = format(selectedDate, 'yyyy');
      break;
    default:
      label = '';
  }

  return (
    <div className="flex flex-col bg-gray-100 p-2 space-y-2 text-sm">
      {/* Row 1: nav + picker */}
      <div className="flex items-center space-x-2">
        <button onClick={onPrev} className="px-1 py-0.5 border rounded">
          ‹
        </button>
        <button onClick={onToday} className="px-1 py-0.5 border rounded">
          Today
        </button>
        <button onClick={onNext} className="px-1 py-0.5 border rounded">
          ›
        </button>

        <input
          type="date"
          value={dateInput}
          onChange={handleDateChange}
          className="ml-4 border rounded p-1 text-sm"
        />
      </div>

      {/* Row 2: big label */}
      <div className="text-center font-semibold text-lg">{label}</div>
    </div>
  );
}
