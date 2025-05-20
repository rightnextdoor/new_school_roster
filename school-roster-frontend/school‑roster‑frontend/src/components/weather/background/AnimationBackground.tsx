// src/components/weather/background/AnimationBackground.tsx
import React from 'react';
import Lottie from 'lottie-react';
import { getWeatherAnimation } from '../../../utils/getWeatherAnimation';

interface AnimationBackgroundProps {
  weatherMain: string;
  weatherDescription: string;
}

const AnimationBackground: React.FC<AnimationBackgroundProps> = ({
  weatherMain,
  weatherDescription,
}) => {
  const animationData = getWeatherAnimation({
    main: weatherMain,
    description: weatherDescription,
  });

  return (
    <div className="absolute inset-0">
      <Lottie
        animationData={animationData}
        loop
        autoplay
        className="w-full h-full"
      />
    </div>
  );
};

export default AnimationBackground;
