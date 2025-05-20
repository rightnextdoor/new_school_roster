// src/utils/normalizeWeatherString.ts

/**
 * Normalize a weather string by:
 *  - lowercasing
 *  - replacing any non-alphanumeric with a space
 *  - collapsing multiple spaces
 *  - trimming edges
 */
export function normalizeWeatherString(input: string): string {
  return input
    .toLowerCase()
    .replace(/[^a-z0-9]+/g, ' ') // everything except letters+digits → space
    .replace(/\s+/g, ' ') // collapse runs of spaces
    .trim(); // remove leading/trailing spaces
}
