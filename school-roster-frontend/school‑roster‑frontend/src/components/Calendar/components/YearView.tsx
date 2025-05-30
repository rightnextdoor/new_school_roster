// src/components/Calendar/components/YearView.tsx

import React from 'react';
import { CalendarEvent } from '../../../types/CalendarEvent';
import { format, addMonths, startOfYear } from 'date-fns';

interface YearViewProps {
  date: Date;
  events: CalendarEvent[];
  selectedDate: Date;
  onPickDate(date: Date): void;
}

export default function YearView({
  date,
  events,
  selectedDate,
  onPickDate,
}: YearViewProps) {
  const yearStart = startOfYear(date);
  const months = Array.from({ length: 12 }).map((_, i) =>
    addMonths(yearStart, i)
  );

  return (
    <div className="grid grid-cols-3 gap-4">
      {months.map((monthDate) => {
        const monthKey = format(monthDate, 'yyyy-MM');
        const isCurrent = monthKey === format(selectedDate, 'yyyy-MM');
        // filter events in this month
        const monthEvents = events.filter((e) =>
          e.start.toDateString().startsWith(format(monthDate, 'yyyy-MM'))
        );

        return (
          <div
            key={monthKey}
            data-month={monthKey}
            onClick={() => onPickDate(monthDate)}
            className={`
              p-3 border rounded cursor-pointer
              ${isCurrent ? 'bg-blue-100' : 'bg-white'}
              hover:bg-blue-200 transition-colors duration-150
            `}
          >
            <div className="font-semibold text-center">
              {format(monthDate, 'MMMM yyyy')}
            </div>
            <div className="mt-2 text-xs">
              {monthEvents.length} event{monthEvents.length !== 1 && 's'}
            </div>
          </div>
        );
      })}
    </div>
  );
}
