import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

interface AuthContextType {
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const SESSION_TIMEOUT = 60 * 60 * 1000; // 1 hour in milliseconds

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem('token')
  );
  const navigate = useNavigate();

  // Helper: Update last active time
  const updateLastActive = () => {
    localStorage.setItem('lastActive', Date.now().toString());
  };

  // Check if the session has expired
  const checkSessionTimeout = () => {
    const lastActive = parseInt(localStorage.getItem('lastActive') || '0', 10);
    if (Date.now() - lastActive > SESSION_TIMEOUT) {
      logout();
    } else {
      updateLastActive();
    }
  };

  // Auto-login if token exists
  useEffect(() => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      navigate('/dashboard');
    }
  }, [token, navigate]);

  // Set up session timeout checker
  useEffect(() => {
    const interval = setInterval(checkSessionTimeout, 1000 * 60); // Check every minute
    return () => clearInterval(interval);
  }, []);

  // Listen for user activity to reset the session timeout
  useEffect(() => {
    const handleActivity = () => updateLastActive();
    window.addEventListener('mousemove', handleActivity);
    window.addEventListener('keydown', handleActivity);

    return () => {
      window.removeEventListener('mousemove', handleActivity);
      window.removeEventListener('keydown', handleActivity);
    };
  }, []);

  // Login function
  const login = async (email: string, password: string) => {
    try {
      const response = await api.post('/api/auth/login', { email, password });
      const token = response.data;

      setToken(token);
      localStorage.setItem('token', token); // Persist token
      updateLastActive();
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Login failed', error);
      alert('Invalid credentials. Please try again.');
    }
  };

  // Logout function
  const logout = () => {
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('lastActive');
    delete api.defaults.headers.common['Authorization'];
    navigate('/login');
  };

  return (
    <AuthContext.Provider
      value={{ token, isAuthenticated: !!token, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
