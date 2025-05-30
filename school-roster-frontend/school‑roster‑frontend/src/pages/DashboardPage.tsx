// src/pages/DashboardPage.tsx
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import WeatherWidget from '../components/weather/WeatherWidget';
import { getMyProfile } from '../services/profileApi';
import type {
  StudentProfile,
  NonStudentProfile,
} from '../components/profile/types';
import { CalendarContainer } from '../components/Calendar';
import { ViewType } from '../types/ViewType';

export default function DashboardPage() {
  const { user, authLoading } = useAuth();
  const navigate = useNavigate();

  // Calendar persistence keys (use empty string if user undefined)
  const uid = user?.id ?? '';
  const savedDateKey = `calendar-date-${uid}`;
  const savedViewKey = `calendar-view-${uid}`;

  // Calendar settings persistence (unconditionally declared to satisfy Rules of Hooks)
  const [savedDate, setSavedDate] = useState<Date>(() => {
    const stored = localStorage.getItem(savedDateKey);
    return stored ? new Date(stored) : new Date();
  });
  const [savedView, setSavedView] = useState<ViewType>(() => {
    const stored = localStorage.getItem(savedViewKey) as ViewType;
    return stored || 'month';
  });

  useEffect(() => {
    if (uid) {
      localStorage.setItem(savedDateKey, savedDate.toISOString());
    }
  }, [savedDate, savedDateKey, uid]);

  useEffect(() => {
    if (uid) {
      localStorage.setItem(savedViewKey, savedView);
    }
  }, [savedView, savedViewKey, uid]);

  // Profile state
  const [profile, setProfile] = useState<
    StudentProfile | NonStudentProfile | null
  >(null);
  useEffect(() => {
    if (!authLoading) {
      getMyProfile()
        .then(setProfile)
        .catch(() => setProfile(null));
    }
  }, [authLoading]);

  const [graphType, setGraphType] = useState<'bar' | 'pie'>('bar');

  if (authLoading || !user) {
    return (
      <div className="flex h-screen items-center justify-center">Loading…</div>
    );
  }

  // Profile display values
  const photoUrl = profile?.profilePicture ?? '/placeholder-avatar.png';
  const displayName = profile
    ? [profile.firstName, profile.middleName, profile.lastName]
        .filter(Boolean)
        .join(' ')
    : 'Profile needs to be updated';
  const displayRole = user.roles?.length
    ? user.roles.join(', ').replace(/_/g, ' ')
    : 'User';

  return (
    <div className="flex h-screen">
      {/* Sidebar */}
      <aside className="w-1/4 bg-gray-100 p-4 space-y-4 shadow-md">
        <div className="bg-white p-4 rounded-md shadow-md">
          <h2 className="text-lg font-semibold mb-2">User Info</h2>
          {profile ? (
            <div className="flex flex-col items-center space-y-3">
              <img
                src={photoUrl}
                alt="Profile"
                className="w-36 h-36 rounded-full object-cover bg-gray-200"
              />
              <div className="text-center">
                <p className="text-xl font-medium">{displayName}</p>
                <p className="text-gray-600">{displayRole}</p>
                <button
                  onClick={() =>
                    navigate(`/profile/${user.id}`, {
                      state: { from: '/dashboard' },
                    })
                  }
                  className="mt-2 text-sm text-teal-600 hover:underline"
                >
                  View Profile
                </button>
              </div>
            </div>
          ) : (
            <div className="flex flex-col items-center space-y-3">
              <div className="w-36 h-36 rounded-full bg-gray-200 flex items-center justify-center">
                <p className="text-gray-500">No Profile</p>
              </div>
              <button
                onClick={() =>
                  navigate(`/profile/${user.id}`, {
                    state: { from: '/dashboard' },
                  })
                }
                className="mt-2 text-sm text-teal-600 hover:underline"
              >
                Create Profile
              </button>
            </div>
          )}
        </div>
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

      {/* Main Content */}
      <div className="flex-1 p-8 space-y-6">
        {/* Top Row: Calendar & Weather */}
        <div className="flex justify-end space-x-4 h-[28rem]">
          {/* Calendar Card */}
          <div className="bg-white p-4 rounded-md shadow-md w-1/2 h-full flex flex-col">
            <div className="mt-2 bg-gray-50 p-3 rounded flex-1 flex flex-col overflow-hidden">
              <CalendarContainer
                userId={user.id}
                profileCountry={profile?.address.country ?? ''}
                initialDate={savedDate}
                initialView={savedView}
                onDateChange={setSavedDate}
                onViewChange={setSavedView}
              />
            </div>
          </div>

          {/* Weather Card */}
          <div className="bg-white p-4 rounded-md shadow-md w-1/2 h-full flex flex-col">
            <WeatherWidget
              userId={user.id}
              defaultCity={profile?.address.cityMunicipality}
            />
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
              [Graph Placeholder – {graphType.toUpperCase()}]
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
