/* eslint-disable @typescript-eslint/no-non-null-asserted-optional-chain */
/* eslint-disable @typescript-eslint/no-explicit-any */
// src/index.tsx

import React, { ReactNode, useState, useEffect } from 'react';
import {
  Routes,
  Route,
  Navigate,
  useLocation,
  useParams,
} from 'react-router-dom';
import AuthLayout from '../layouts/AuthLayout';
import AppLayout from '../layouts/AppLayout';
import LoginPage from '../pages/LoginPage';
import DashboardPage from '../pages/DashboardPage';
import StudentsListPage from '../components/students/StudentsListPage';
import ProfilePage from '../components/profile/ProfilePage';
import RosterOverviewPage from '../components/roster/pages/RosterOverviewPage';
import CreateRosterPage from '../components/roster/pages/CreateRosterPage';
import UpdateRosterPage from '../components/roster/pages/UpdateRosterPage';
import RosterViewPage from '../components/roster/pages/RosterViewPage';
import { useAuth } from '../contexts/AuthContext';
import { getUserById, User } from '../services/userApi';

function PrivateRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  return <>{children}</>;
}

function RoleProtectedRoute({
  children,
  allowedRoles,
}: {
  children: ReactNode;
  allowedRoles: string[];
}) {
  const { user } = useAuth();
  const roles = user?.roles ?? [];
  if (!allowedRoles.some((r) => roles.includes(r))) {
    return <Navigate to="/dashboard" replace />;
  }
  return <>{children}</>;
}

// Pulls userId from URL, or falls back to state.user, then to logged-in user
function ProfileWrapper({ edit }: { edit?: boolean }) {
  const { userId } = useParams<{ userId: string }>();
  const loc = useLocation();
  const stateUser = (loc.state as any)?.user as User | undefined;
  const { user: loginUser } = useAuth();

  const targetId = userId ?? loginUser?.id!;
  const [profileUser, setProfileUser] = useState<User | null>(
    stateUser ?? null
  );

  useEffect(() => {
    if (!profileUser || profileUser.id !== targetId) {
      getUserById(targetId).then(setProfileUser);
    }
  }, [targetId, profileUser]);

  if (!profileUser) {
    return <p>Loading user…</p>;
  }

  return (
    <ProfilePage
      user={profileUser}
      editMode={edit}
      backTo={(loc.state as any)?.from || '/dashboard'}
    />
  );
}

export default function Router() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route element={<AuthLayout />}>
        <Route path="/login" element={<LoginPage />} />
      </Route>

      <Route
        element={
          <PrivateRoute>
            <AppLayout />
          </PrivateRoute>
        }
      >
        <Route path="/dashboard" element={<DashboardPage />} />

        <Route path="/roster" element={<RosterOverviewPage />} />
        <Route path="/roster/create" element={<CreateRosterPage />} />
        <Route path="/roster/update/:id" element={<UpdateRosterPage />} />
        <Route path="/roster/view/:id" element={<RosterViewPage />} />

        <Route
          path="/students"
          element={
            <RoleProtectedRoute
              allowedRoles={[
                'TEACHER',
                'TEACHER_LEAD',
                'ADMINISTRATOR',
                'ADMIN',
              ]}
            >
              <StudentsListPage />
            </RoleProtectedRoute>
          }
        />

        <Route path="/profile/:userId" element={<ProfileWrapper />} />
        <Route path="/profile/:userId/edit" element={<ProfileWrapper edit />} />
      </Route>

      <Route path="*" element={<Navigate to="/login" replace />} />
    </Routes>
  );
}
