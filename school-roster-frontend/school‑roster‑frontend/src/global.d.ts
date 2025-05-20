// src/global.d.ts
// Allow importing JSON modules with unknown content (avoid `any`)
declare module '*.json' {
  const value: unknown;
  export default value;
}
