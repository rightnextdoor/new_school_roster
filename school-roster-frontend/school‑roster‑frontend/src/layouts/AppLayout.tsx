import { Outlet } from 'react-router-dom';

export default function AppLayout() {
  return (
    <div className="flex flex-col min-h-screen">
      {/* Top Navbar with School Name */}
      <nav className="bg-gray-800 text-white shadow px-4 py-2 flex justify-between items-center">
        <div className="flex space-x-4">
          <span className="text-lg font-semibold">
            Sibonga Central Elementary School
          </span>
          <div className="space-x-4">
            <a href="/dashboard" className="hover:text-green-300">
              Dashboard
            </a>
            <a href="/rosters" className="hover:text-green-300">
              Rosters
            </a>
            <a href="/grades" className="hover:text-green-300">
              Grades
            </a>
          </div>
        </div>
        <div>
          <a
            href="/login"
            className="bg-red-500 px-3 py-1 rounded text-white hover:bg-red-600"
          >
            Logout
          </a>
        </div>
      </nav>

      {/* Main Content with Gradient Background */}
      <main className="flex-1 p-4 bg-gradient-to-b from-blue-500 via-purple-500 to-pink-500">
        <Outlet />
      </main>
    </div>
  );
}
