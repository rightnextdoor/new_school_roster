// src/components/Calendar/components/MonthView.tsx

import React from 'react';
import { CalendarEvent } from '../../../types/CalendarEvent';
import { getVisibleRange } from '../../../utils/dateUtils';
import styles from '../styles/calendar.module.css';
import { format } from 'date-fns';

interface MonthViewProps {
  date: Date;
  events: CalendarEvent[];
  selectedDate: Date;
  onPickDate(date: Date): void;
}

export default function MonthView({
  date,
  events,
  selectedDate,
  onPickDate,
}: MonthViewProps) {
  const days = getVisibleRange(date, 'month');

  return (
    <div className={styles.monthView}>
      <div className="grid grid-cols-7 border-b">
        {['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'].map((d) => (
          <div key={d} className="p-2 text-center font-semibold border-r">
            {d}
          </div>
        ))}
      </div>
      <div className="grid grid-cols-7">
        {days.map((day) => {
          const dayKey = format(day, 'yyyy-MM-dd');
          const isCurrentMonth = day.getMonth() === date.getMonth();
          const isSelected = day.toDateString() === selectedDate.toDateString();
          const dayEvents = events.filter(
            (e) => e.start.toDateString() === day.toDateString()
          );

          return (
            <div
              key={day.toISOString()}
              data-day={dayKey}
              onClick={() => onPickDate(day)}
              className={`
                p-1 border-r border-t h-24 overflow-auto cursor-pointer
                ${!isCurrentMonth ? 'text-gray-400' : 'text-gray-800'}
                ${isSelected ? 'bg-blue-100' : 'bg-white'}
                hover:bg-blue-200
                transition-colors duration-150
              `}
            >
              <div className="font-medium text-sm mb-1">{format(day, 'd')}</div>
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
    </div>
  );
}
