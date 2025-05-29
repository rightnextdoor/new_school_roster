// src/components/profile/AddressAutocomplete.tsx

import React, { useRef, ChangeEvent } from 'react';
import { Autocomplete } from '@react-google-maps/api';
import type { Address } from './types';

interface Props {
  value: Address;
  onChange: (address: Address) => void;
}

const AddressAutocomplete: React.FC<Props> = ({ value, onChange }) => {
  const autocompleteRef = useRef<google.maps.places.Autocomplete | null>(null);

  const handleLoad = (autocomplete: google.maps.places.Autocomplete) => {
    autocompleteRef.current = autocomplete;
  };

  const handlePlaceChanged = () => {
    const place = autocompleteRef.current?.getPlace();
    if (!place?.address_components) return;
    const comps = place.address_components;
    const getComp = (type: string) =>
      comps.find((c) => c.types.includes(type))?.long_name || '';

    const newAddress: Address = {
      streetAddress: `${getComp('street_number')} ${getComp('route')}`.trim(),
      subdivision: getComp('sublocality_level_1'),
      cityMunicipality: getComp('locality'),
      provinceState: getComp('administrative_area_level_1'),
      country: getComp('country'),
      zipCode: getComp('postal_code'),
    };

    onChange(newAddress);
  };

  return (
    <Autocomplete onLoad={handleLoad} onPlaceChanged={handlePlaceChanged}>
      <input
        type="text"
        placeholder="Start typing your address…"
        value={value.streetAddress}
        onChange={
          (e: ChangeEvent<HTMLInputElement>) =>
            onChange({ ...value, streetAddress: e.target.value }) // <-- spread here
        }
        className="address-input"
      />
    </Autocomplete>
  );
};

export default AddressAutocomplete;
