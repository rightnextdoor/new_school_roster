// src/components/Calendar/components/CalendarView.tsx

import React, { useRef, useEffect } from 'react';
import { ViewType } from '../../../types/ViewType';
import { CalendarEvent } from '../../../types/CalendarEvent';
import DayView from './DayView';
import WeekView from './WeekView';
import MonthView from './MonthView';
import YearView from './YearView';
import { format } from 'date-fns';

interface CalendarViewProps {
  date: Date;
  view: ViewType;
  events: CalendarEvent[];
  selectedDate: Date;
  onPickDate(date: Date): void;
  onPrev(): void;
  onNext(): void;
}

export default function CalendarView({
  date,
  view,
  events,
  selectedDate,
  onPickDate,
  onPrev,
  onNext,
}: CalendarViewProps) {
  const containerRef = useRef<HTMLDivElement>(null);

  // Scroll "today" into view in month mode
  useEffect(() => {
    if (view === 'month') {
      const key = format(selectedDate, 'yyyy-MM-dd');
      const cell = containerRef.current?.querySelector<HTMLElement>(
        `[data-day="${key}"]`
      );
      if (cell) {
        cell.scrollIntoView({
          behavior: 'smooth',
          block: 'center',
          inline: 'center',
        });
      }
    }
  }, [selectedDate, view]);

  // Our wheel handler that calls preventDefault
  const handleWheel = (e: WheelEvent) => {
    // stop the page from scrolling
    e.preventDefault();
    e.stopPropagation();

    if (view === 'month') {
      const el = containerRef.current!;
      const { scrollTop, scrollHeight, clientHeight } = el;
      if (scrollTop <= 0 && e.deltaY < 0) {
        onPrev();
      } else if (scrollTop + clientHeight >= scrollHeight && e.deltaY > 0) {
        onNext();
      }
    } else {
      if (e.deltaY < 0) onPrev();
      else if (e.deltaY > 0) onNext();
    }
  };

  // Attach a non-passive listener so preventDefault is allowed
  useEffect(() => {
    const el = containerRef.current;
    if (!el) return;
    el.style.overscrollBehavior = 'contain';
    el.addEventListener('wheel', handleWheel, { passive: false });
    return () => {
      el.removeEventListener('wheel', handleWheel);
    };
  }, [view, onPrev, onNext]);

  // render the appropriate inner view
  const inner = (() => {
    switch (view) {
      case 'day':
        return (
          <DayView
            date={date}
            events={events.filter(
              (e) => e.start.toDateString() === date.toDateString()
            )}
          />
        );
      case 'week':
        return (
          <WeekView
            date={date}
            events={events}
            selectedDate={selectedDate}
            onPickDate={onPickDate}
          />
        );
      case 'month':
        return (
          <MonthView
            date={date}
            events={events}
            selectedDate={selectedDate}
            onPickDate={onPickDate}
          />
        );
      case 'year':
        return (
          <YearView
            date={date}
            events={events}
            selectedDate={selectedDate}
            onPickDate={onPickDate}
          />
        );
      default:
        return null;
    }
  })();

  return (
    <div ref={containerRef} className="flex-1 overflow-auto">
      {inner}
    </div>
  );
}
