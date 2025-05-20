import React from 'react';
import { Outlet } from 'react-router-dom';

export default function AuthLayout() {
  return (
    <div className="flex min-h-screen">
      {/* Left: logo & motto */}
      <div className="w-1/2 bg-yellow-400 flex flex-col items-center justify-between p-8">
        {/* Top Section: Logo and School Name */}
        <div className="flex flex-col items-center justify-center flex-grow">
          <img
            src="/src/assets/school-logo.jpg"
            alt="School Logo"
            className="w-72 h-72 mb-4"
          />
          <h1 className="text-5xl font-extrabold text-white mb-1 text-center">
            Sibonga Central Elementary School
          </h1>
          <p className="text-xl text-white text-center">
            Empowering learning since 1945
          </p>
        </div>

        {/* Bottom Section: Motto */}
        <div className="mb-8 px-8">
          <p className="text-lg text-white text-center">
            We are committed to creating collaborative and innovative learning
            experiences, through which students develop critical thinking
            skills, achieve or surpass the Common Core Standards, and develop
            lifelong learning habits and positive mindsets.
          </p>
        </div>
      </div>

      {/* Right: Picture and Login Form */}
      <div className="w-1/2 flex flex-col justify-center items-center bg-gray-50 p-8">
        {/* Top Half: School Background Image */}
        <div
          className="w-full h-1/2 bg-cover bg-center"
          style={{
            backgroundImage: `url('/src/assets/school-background.jpg')`,
          }}
        ></div>

        {/* Login Box */}
        <div className="mt-6 w-full max-w-lg">
          {' '}
          {/* Added margin-top to move it down */}
          <Outlet />
        </div>
      </div>
    </div>
  );
}
