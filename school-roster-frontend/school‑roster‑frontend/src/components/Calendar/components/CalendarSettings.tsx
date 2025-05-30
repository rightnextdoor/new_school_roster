// src/components/Calendar/components/CalendarSettings.tsx

import React, { useState, useEffect } from 'react';
import AsyncSelect from 'react-select/async';
import countries from 'i18n-iso-countries';
import enLocale from 'i18n-iso-countries/langs/en.json';
import { ViewType } from '../../../types/ViewType';

countries.registerLocale(enLocale);

type Option = { value: string; label: string };

interface CalendarSettingsProps {
  userId: string;
  profileCountry: string;
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
  onCountryChange: (country: string) => void;
}

export default function CalendarSettings({
  userId,
  profileCountry,
  currentView,
  onViewChange,
  onCountryChange,
}: CalendarSettingsProps) {
  const storageKey = `calendar-country-${userId}`;

  const [selectedCountry, setSelectedCountry] = useState<Option | null>(null);
  const [selectedView, setSelectedView] = useState<ViewType>(currentView);

  // Load initial country
  useEffect(() => {
    const saved = localStorage.getItem(storageKey) || profileCountry;
    setSelectedCountry({
      value: saved,
      label: `${countries.getName(saved, 'en') || saved} (${saved})`,
    });
  }, [storageKey, profileCountry]);

  const loadOptions = (
    inputValue: string,
    callback: (options: Option[]) => void
  ) => {
    const all = countries.getAlpha2Codes();
    const opts = Object.keys(all)
      .map((code) => ({
        value: code,
        label: `${countries.getName(code, 'en')} (${code})`,
      }))
      .filter((opt) =>
        opt.label.toLowerCase().includes(inputValue.toLowerCase())
      )
      .slice(0, 20);
    callback(opts);
  };

  const handleCountrySelect = (opt: Option | null) => {
    if (!opt) return;
    setSelectedCountry(opt);
    localStorage.setItem(storageKey, opt.value);
    onCountryChange(opt.value);
  };

  const handleViewSelect = (view: ViewType) => {
    setSelectedView(view);
    onViewChange(view);
  };

  const VIEWS: ViewType[] = ['day', 'week', 'month', 'year'];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b pb-2">
        <h3 className="text-lg font-semibold text-gray-800">
          Calendar Settings
        </h3>
      </div>

      {/* Country Section */}
      <div className="space-y-2">
        <label className="block text-sm font-medium text-gray-600">
          Holiday Country
        </label>
        <AsyncSelect
          cacheOptions
          defaultOptions
          loadOptions={loadOptions}
          value={selectedCountry}
          onChange={handleCountrySelect}
          placeholder="Search country..."
          className="react-select-container"
          classNamePrefix="react-select"
        />
      </div>

      {/* View Section */}
      <div className="space-y-2">
        <label className="block text-sm font-medium text-gray-600">
          Default View
        </label>
        <div className="grid grid-cols-4 gap-2">
          {VIEWS.map((v) => (
            <button
              key={v}
              type="button"
              onClick={() => handleViewSelect(v)}
              className={`py-2 text-center border rounded-md transition 
                ${
                  selectedView === v
                    ? 'bg-blue-600 text-white border-blue-600'
                    : 'bg-white text-gray-700 hover:bg-gray-50'
                }`}
            >
              {v.charAt(0).toUpperCase() + v.slice(1)}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
