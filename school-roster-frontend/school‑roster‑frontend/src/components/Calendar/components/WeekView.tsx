// src/components/Calendar/components/WeekView.tsx

import React from 'react';
import { CalendarEvent } from '../../../types/CalendarEvent';
import { format, startOfWeek, addDays } from 'date-fns';

interface WeekViewProps {
  date: Date;
  events: CalendarEvent[];
  selectedDate: Date;
  onPickDate(date: Date): void;
}

export default function WeekView({
  date,
  events,
  selectedDate,
  onPickDate,
}: WeekViewProps) {
  const weekStart = startOfWeek(date, { weekStartsOn: 0 });
  const days = Array.from({ length: 7 }).map((_, i) => addDays(weekStart, i));

  return (
    <div className="grid grid-cols-7 border">
      {days.map((day) => {
        const dayKey = format(day, 'yyyy-MM-dd');
        const isSelected = dayKey === format(selectedDate, 'yyyy-MM-dd');
        const dayEvents = events.filter(
          (e) => e.start.toDateString() === day.toDateString()
        );

        return (
          <div
            key={dayKey}
            data-day={dayKey}
            onClick={() => onPickDate(day)}
            className={`
              p-2 border-r border-b h-32 overflow-auto cursor-pointer
              ${isSelected ? 'bg-blue-100' : 'bg-white'}
              hover:bg-blue-200 transition-colors duration-150
            `}
          >
            {/* Day name on its own line */}
            <div className="text-xs font-semibold text-center">
              {format(day, 'EEE')}
            </div>
            {/* Date number on its own line */}
            <div className="text-lg font-medium text-center mb-2">
              {format(day, 'd')}
            </div>
            {/* Events */}
            {dayEvents.map((event) => (
              <div
                key={event.id}
                className="text-xs mb-1 whitespace-normal break-words"
                title={event.summary}
              >
                • {event.summary}
              </div>
            ))}
          </div>
        );
      })}
    </div>
  );
}
