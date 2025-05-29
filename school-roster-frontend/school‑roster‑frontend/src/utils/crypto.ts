// src/utils/crypto.ts
import CryptoJS from 'crypto-js';

const HEX_KEY = import.meta.env.VITE_GOV_ID_KEY!;
if (!HEX_KEY) throw new Error('Missing VITE_GOV_ID_KEY in .env');

export const KEY = CryptoJS.enc.Hex.parse(HEX_KEY);

/**
 * Quick check for a valid Base64 string.
 */
function isValidBase64(str: string): boolean {
  // Only A–Z, a–z, 0–9, +, /, and up to two = paddings
  return /^[A-Za-z0-9+/]+={0,2}$/.test(str);
}

/**
 * Heuristic: returns true if the string is a Base64-encoded blob
 * containing at least a 16-byte IV + some ciphertext.
 */
function isLikelyEncrypted(str: string): boolean {
  if (typeof str !== 'string') return false;
  if (!isValidBase64(str)) return false;
  if (str.length % 4 !== 0) return false; // Base64 padding rule
  if (str.length < 24) return false; // 16-byte IV → ~24 Base64 chars
  try {
    const bytes = CryptoJS.enc.Base64.parse(str);
    return bytes.sigBytes > 16; // more than just IV
  } catch {
    return false;
  }
}

/**
 * Encrypts plain → Base64(IV||ciphertext).
 * If the input already looks like an encrypted blob, returns it unchanged.
 */
export function encryptField(plaintext: string): string {
  if (isLikelyEncrypted(plaintext)) {
    return plaintext;
  }
  const iv = CryptoJS.lib.WordArray.random(16);
  const ciphertext = CryptoJS.AES.encrypt(plaintext, KEY, {
    iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.Pkcs7,
  }).ciphertext;
  return CryptoJS.enc.Base64.stringify(iv.concat(ciphertext));
}

/**
 * Decrypts Base64(IV||ciphertext) → plain UTF-8.
 * If the input doesn’t match our encrypted-blob pattern, returns it unchanged.
 */
export function decryptField(blob: string): string {
  if (!isLikelyEncrypted(blob)) {
    return blob;
  }
  try {
    const combined = CryptoJS.enc.Base64.parse(blob);
    const iv = CryptoJS.lib.WordArray.create(combined.words.slice(0, 4), 16);
    const ct = CryptoJS.lib.WordArray.create(
      combined.words.slice(4),
      combined.sigBytes - 16
    );
    const cipherParams = CryptoJS.lib.CipherParams.create({ ciphertext: ct });
    const decrypted = CryptoJS.AES.decrypt(cipherParams, KEY, {
      iv,
      mode: CryptoJS.mode.CBC,
      padding: CryptoJS.pad.Pkcs7,
    });
    return decrypted.toString(CryptoJS.enc.Utf8);
  } catch {
    return blob;
  }
}
