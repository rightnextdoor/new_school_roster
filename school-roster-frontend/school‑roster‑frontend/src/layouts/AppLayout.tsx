// src/layouts/AppLayout.tsx
import { Outlet, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

export default function AppLayout() {
  const { logout, user } = useAuth();
  const userRoles = user?.roles;

  const handleLogout = () => {
    logout();
  };

  return (
    <div className="flex flex-col min-h-screen">
      <nav className="bg-gray-800 text-white shadow px-4 py-2 flex justify-between items-center">
        <div className="flex space-x-4">
          <span className="text-lg font-semibold">
            Sibonga Central Elementary School
          </span>
          <div className="space-x-4">
            <Link to="/dashboard" className="hover:text-green-300">
              Dashboard
            </Link>
            {['TEACHER', 'TEACHER_LEAD', 'ADMINISTRATOR', 'ADMIN'].some(
              (role) => userRoles?.includes(role)
            ) && (
              <Link to="/students" className="hover:text-green-300">
                Students
              </Link>
            )}
            <Link to="/roster" className="hover:text-green-300">
              Rosters
            </Link>
            <Link to="/grades" className="hover:text-green-300">
              Grades
            </Link>
          </div>
        </div>
        <div>
          <button
            onClick={handleLogout}
            className="bg-red-500 px-3 py-1 rounded text-white hover:bg-red-600"
          >
            Logout
          </button>
        </div>
      </nav>
      <main className="flex-1 p-4 bg-gradient-to-b from-blue-500 via-purple-500 to-pink-500">
        <Outlet />
      </main>
    </div>
  );
}
