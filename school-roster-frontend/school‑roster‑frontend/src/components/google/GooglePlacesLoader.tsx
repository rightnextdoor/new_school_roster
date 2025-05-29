// src/components/google/GooglePlacesLoader.tsx
import React, { useState, useEffect } from 'react';
import { useJsApiLoader } from '@react-google-maps/api';
import { fetchGooglePlacesKey } from '../../services/configService';

interface Props {
  children: React.ReactNode;
}

// This inner component only ever sees one apiKey,
// so useJsApiLoader is only called once with stable options.
const ScriptLoader: React.FC<{ apiKey: string; children: React.ReactNode }> = ({
  apiKey,
  children,
}) => {
  const { isLoaded, loadError } = useJsApiLoader({
    id: 'google-places-script',
    googleMapsApiKey: apiKey,
    libraries: ['places'],
  });

  if (loadError) {
    return <div className="text-red-600">Google Maps load error</div>;
  }
  if (!isLoaded) {
    return <div>Loading address autocomplete…</div>;
  }
  return <>{children}</>;
};

const GooglePlacesLoader: React.FC<Props> = ({ children }) => {
  const [apiKey, setApiKey] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchGooglePlacesKey()
      .then(setApiKey)
      .catch((err) => {
        console.error(err);
        setError('Could not load Google Places key');
      });
  }, []);

  if (error) {
    return <div className="text-red-600">{error}</div>;
  }
  if (!apiKey) {
    // still fetching your key from your backend
    return <div>Loading address autocomplete…</div>;
  }

  // now apiKey is non-null, mount the real loader
  return <ScriptLoader apiKey={apiKey}>{children}</ScriptLoader>;
};

export default GooglePlacesLoader;
