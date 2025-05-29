// // src/components/weather/AnimationTest.tsx
// import React, { useState } from 'react';
// import AnimationBackground from './background/AnimationBackground';

// // List of all supported condition keys
// const CONDITIONS = [
//   'Clear',
//   'Clouds',
//   'Drizzle',
//   'Rain',
//   'HeavyRain',
//   'Thunderstorm',
//   'Snow',
//   'HeavySnow',
//   'Sleet',
//   'Tornado',
//   'Hurricane',
//   'Fog',
//   'Windy',
// ] as const;

// const AnimationTest: React.FC = () => {
//   const [condition, setCondition] =
//     useState<(typeof CONDITIONS)[number]>('Clear');

//   return (
//     <div className="p-4 flex h-screen space-x-4">
//       {/* Sidebar */}
//       <div className="w-1/4 max-w-xs">
//         <label
//           htmlFor="condition-select"
//           className="block mb-2 font-medium text-gray-700"
//         >
//           Select Weather Condition:
//         </label>
//         <select
//           id="condition-select"
//           value={condition}
//           // eslint-disable-next-line @typescript-eslint/no-explicit-any
//           onChange={(e) => setCondition(e.target.value as any)}
//           className="w-full border rounded p-2"
//         >
//           {CONDITIONS.map((c) => (
//             <option key={c} value={c}>
//               {c}
//             </option>
//           ))}
//         </select>
//       </div>

//       {/* Animation preview */}
//       <div className="flex-1 relative rounded-lg overflow-hidden shadow-lg">
//         {/*
//           AnimationBackground is styled with `absolute inset-0`,
//           so it will fill this container completely.
//         */}
//         <AnimationBackground condition={condition} />
//       </div>
//     </div>
//   );
// };

// export default AnimationTest;
