// src/pages/AddressAutocompleteTest.tsx

import React, { useState } from 'react';
import GooglePlacesLoader from '../components/google/GooglePlacesLoader';
import AddressAutocomplete from '../components/profile/AddressAutocomplete';
import type { Address } from '../components/profile/types';

const AddressAutocompleteTest: React.FC = () => {
  const [address, setAddress] = useState<Address>({
    streetAddress: '',
    subdivision: '',
    cityMunicipality: '',
    provinceState: '',
    country: '',
    zipCode: '',
  });

  return (
    <GooglePlacesLoader>
      <div style={{ padding: '2rem', maxWidth: '600px', margin: '0 auto' }}>
        <h1>Address Autocomplete Test</h1>
        <AddressAutocomplete value={address} onChange={setAddress} />
        <pre
          style={{
            marginTop: '1rem',
            background: '#f5f5f5',
            padding: '1rem',
            borderRadius: '4px',
          }}
        >
          {JSON.stringify(address, null, 2)}
        </pre>
      </div>
    </GooglePlacesLoader>
  );
};

export default AddressAutocompleteTest;
