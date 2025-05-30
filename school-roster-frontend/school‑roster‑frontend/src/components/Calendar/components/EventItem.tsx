// src/components/Calendar/components/EventItem.tsx

import React from 'react';
import { CalendarEvent } from '../../../types/CalendarEvent';

interface EventItemProps {
  event: CalendarEvent;
}

export default function EventItem({ event }: EventItemProps) {
  return (
    <div
      className={`flex items-center space-x-1 py-1 text-sm rounded px-1 ${
        event.isHoliday
          ? 'text-red-600 font-semibold bg-red-50'
          : 'text-gray-800 bg-gray-100'
      }`}
    >
      {event.isHoliday && (
        <span role="img" aria-label="Holiday">
          🎉
        </span>
      )}
      <span>{event.summary}</span>
    </div>
  );
}
