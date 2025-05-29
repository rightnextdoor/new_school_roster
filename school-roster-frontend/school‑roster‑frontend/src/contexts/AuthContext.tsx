// src/contexts/AuthContext.tsx
import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

interface User {
  id: string;
  email: string;
  roles: string[];
}

interface AuthContextType {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
  authLoading: boolean; // ← new flag
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);
const SESSION_TIMEOUT = 60 * 60 * 1000; // 1 hour

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem('token')
  );
  const [user, setUser] = useState<User | null>(null);
  const [authLoading, setAuthLoading] = useState(true); // ← start loading
  const navigate = useNavigate();

  // Fetch current user
  const fetchMe = async () => {
    try {
      const resp = await api.get<User>('/api/auth/me');
      setUser(resp.data);
    } catch {
      logout();
    }
  };

  // Update last active timestamp
  const updateLastActive = () => {
    localStorage.setItem('lastActive', Date.now().toString());
  };

  // Session timeout checker
  const checkSessionTimeout = () => {
    const lastActive = parseInt(localStorage.getItem('lastActive') || '0', 10);
    if (Date.now() - lastActive > SESSION_TIMEOUT) {
      logout();
    } else {
      updateLastActive();
    }
  };

  // Auto-login and fetch "me", then turn off loading
  useEffect(() => {
    (async () => {
      if (token) {
        api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
        await fetchMe();
        if (location.pathname === '/login') {
          navigate('/dashboard');
        }
      }
      setAuthLoading(false); // ← done restoring
    })();
  }, [token, navigate]);

  // Poll for inactivity
  useEffect(() => {
    const interval = setInterval(checkSessionTimeout, 60_000);
    return () => clearInterval(interval);
  }, []);

  // Reset timeout on user activity
  useEffect(() => {
    const onActivity = () => updateLastActive();
    window.addEventListener('mousemove', onActivity);
    window.addEventListener('keydown', onActivity);
    return () => {
      window.removeEventListener('mousemove', onActivity);
      window.removeEventListener('keydown', onActivity);
    };
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const resp = await api.post<string>('/api/auth/login', {
        email,
        password,
      });
      const tok = resp.data;
      setToken(tok);
      localStorage.setItem('token', tok);
      updateLastActive();
      api.defaults.headers.common['Authorization'] = `Bearer ${tok}`;
      await fetchMe();
      navigate('/dashboard');
    } catch (err) {
      console.error('Login failed', err);
      alert('Invalid credentials');
    }
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('lastActive');
    delete api.defaults.headers.common['Authorization'];
    navigate('/login');
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        user,
        isAuthenticated: !!token,
        authLoading, // ← expose it
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be inside AuthProvider');
  return ctx;
};
