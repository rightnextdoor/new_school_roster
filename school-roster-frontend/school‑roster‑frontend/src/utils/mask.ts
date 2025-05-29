// src/utils/mask.ts
/**
 * Masks all but last 4 characters of a string.
 */
export function maskLast4(val: string): string {
  if (!val || val.length < 4) return '••••';
  return `••••-${val.slice(-4)}`;
}
