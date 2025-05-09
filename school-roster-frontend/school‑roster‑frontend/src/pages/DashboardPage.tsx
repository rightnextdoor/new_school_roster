import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';

export default function DashboardPage() {
  const { user } = useAuth();
  const [graphType, setGraphType] = useState<'bar' | 'pie'>('bar');

  return (
    <div className="flex h-screen">
      {/* Sidebar */}
      <aside className="w-1/4 bg-gray-100 p-4 space-y-4 shadow-md">
        {/* User Info */}
        <div className="bg-white p-4 rounded-md shadow-md">
          <h2 className="text-lg font-semibold mb-2">User Info</h2>
          <div className="flex flex-col items-center space-y-3">
            <div className="w-36 h-36 rounded-full bg-gray-300"></div>
            <div className="text-center">
              <p className="text-xl font-medium">
                {user?.name || 'Kevin Pratt'}
              </p>
              <p className="text-gray-600">{user?.role || 'Teacher'}</p>
              <button className="mt-2 text-sm text-blue-600 hover:underline">
                View Profile
              </button>
            </div>
          </div>
        </div>

        {/* Sidebar Navigation Links */}
        <nav className="space-y-2">
          <a
            href="/dashboard"
            className="block px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
          >
            Dashboard
          </a>
          <a
            href="/rosters"
            className="block px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
          >
            Rosters
          </a>
          <a
            href="/grades"
            className="block px-4 py-2 rounded-md bg-gray-200 hover:bg-gray-300"
          >
            Grades
          </a>
        </nav>
      </aside>

      {/* Content Area */}
      <div className="flex-1 p-8 space-y-6">
        {/* Top Widgets (Calendar and Weather) */}
        <div className="flex justify-end space-x-4">
          {/* Calendar */}
          <div className="bg-white p-4 rounded-md shadow-md w-1/2 h-80 flex flex-col justify-center">
            <h2 className="text-lg font-semibold">Calendar</h2>
            <div className="mt-2 bg-gray-50 p-3 rounded h-full flex items-center justify-center">
              <p className="text-gray-600">[Calendar Placeholder]</p>
            </div>
          </div>

          {/* Weather */}
          <div className="bg-white p-4 rounded-md shadow-md w-1/2 h-80 flex flex-col justify-center">
            <h2 className="text-lg font-semibold">Weather</h2>
            <div className="mt-2 bg-gray-50 p-3 rounded h-full flex items-center justify-center">
              <p className="text-gray-600">[Weather Placeholder]</p>
            </div>
          </div>
        </div>

        {/* Performance Overview */}
        <section className="bg-white p-4 rounded-md shadow-md mt-6">
          <div className="flex justify-between items-center">
            <h2 className="text-xl font-semibold">Performance Overview</h2>
            <select
              value={graphType}
              onChange={(e) => setGraphType(e.target.value as 'bar' | 'pie')}
              className="p-1 rounded border-gray-300"
            >
              <option value="bar">Bar Chart</option>
              <option value="pie">Pie Chart</option>
            </select>
          </div>
          <div className="mt-4 h-40 bg-gray-100 rounded-md flex items-center justify-center">
            <p className="text-gray-500">
              [Graph Placeholder - {graphType.toUpperCase()}]
            </p>
          </div>
        </section>

        {/* Upcoming Roster */}
        <section className="bg-white p-4 rounded-md shadow-md mt-6">
          <h2 className="text-xl font-semibold">Upcoming Roster</h2>
          <div className="mt-2 bg-gray-50 p-3 rounded">
            <p className="text-gray-600">[Roster Placeholder]</p>
          </div>
        </section>

        {/* Upcoming Events */}
        <section className="bg-white p-4 rounded-md shadow-md mt-6">
          <h2 className="text-xl font-semibold">Upcoming Events</h2>
          <div className="mt-2 bg-gray-50 p-3 rounded">
            <p className="text-gray-600">[Events Placeholder]</p>
          </div>
        </section>
      </div>
    </div>
  );
}
