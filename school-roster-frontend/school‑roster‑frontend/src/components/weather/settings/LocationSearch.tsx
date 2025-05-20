// src/components/weather/settings/LocationSearch.tsx
import React from 'react';

interface LocationSearchProps {
  citySearch: string;
  onCitySearchChange: (value: string) => void;
  onSearch: () => void;
}

const LocationSearch: React.FC<LocationSearchProps> = ({
  citySearch,
  onCitySearchChange,
  onSearch,
}) => (
  <div className="mb-4">
    <label className="block mb-1 font-medium text-black">Location</label>
    <div className="flex">
      <input
        type="text"
        placeholder="City or ZIP"
        value={citySearch}
        onChange={(e) => onCitySearchChange(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') {
            onSearch();
          }
        }}
        className="flex-1 border rounded-l px-3 py-2"
      />
      <button
        type="button"
        onClick={onSearch}
        className="px-4 bg-blue-600 text-white rounded-r"
      >
        Search
      </button>
    </div>
  </div>
);

export default LocationSearch;
