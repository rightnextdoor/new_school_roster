// src/services/configService.ts

/**
 * Fetches the Google Places API key from your backend.
 */
export async function fetchGooglePlacesKey(): Promise<string> {
  const res = await fetch('/api/config/google-places-key');
  if (!res.ok) {
    throw new Error(`Failed to fetch Google Places key: ${res.statusText}`);
  }
  const payload = await res.json();
  return payload.key;
}
