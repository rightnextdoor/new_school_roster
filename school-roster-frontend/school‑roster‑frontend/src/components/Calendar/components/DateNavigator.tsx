// src/components/Calendar/components/DateNavigator.tsx

import React, { useState } from 'react';

interface DateNavigatorProps {
  selectedDate: Date;
  onPickDate: (date: Date) => void;
}

export default function DateNavigator({
  selectedDate,
  onPickDate,
}: DateNavigatorProps) {
  const [inputValue, setInputValue] = useState<string>(
    selectedDate.toISOString().slice(0, 10)
  );
  const [error, setError] = useState<string>('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
    setError('');
  };

  const handleSubmit = () => {
    const parsed = new Date(inputValue);
    if (isNaN(parsed.getTime())) {
      setError('Invalid date. Please use YYYY-MM-DD.');
    } else {
      onPickDate(parsed);
      setError('');
    }
  };

  return (
    <div className="flex items-center space-x-1">
      <input
        type="date"
        value={inputValue}
        onChange={handleChange}
        className="border rounded p-1"
      />
      <button onClick={handleSubmit} className="px-2 py-1 border rounded">
        Go
      </button>
      {error && <span className="text-red-500 text-sm">{error}</span>}
    </div>
  );
}
