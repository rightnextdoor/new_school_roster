// src/components/Calendar/components/DayView.tsx

import React from 'react';
import { CalendarEvent } from '../../../types/CalendarEvent';
import styles from '../styles/calendar.module.css';
import { format } from 'date-fns';

interface DayViewProps {
  date: Date;
  events: CalendarEvent[];
}

export default function DayView({ date, events }: DayViewProps) {
  // Display hourly slots or simple list
  return (
    <div className={styles.dayView}>
      <h3 className="text-center font-semibold mb-2">
        {format(date, 'MMMM d, yyyy')}
      </h3>
      <ul>
        {events.length === 0 && <li className="p-2">No events</li>}
        {events.map((event) => (
          <li key={event.id} className="p-2 border-b">
            <span className="font-medium">{event.summary}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}
