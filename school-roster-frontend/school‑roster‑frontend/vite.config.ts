// vite.config.ts
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      // everything under /api/weather goes to your Express proxy
      '/api/weather': {
        target: 'http://localhost:3001',
        changeOrigin: true,
        secure: false,
      },
      '/api/holidays': {
        target: 'http://localhost:3001',
        changeOrigin: true,
        secure: false,
      },
      '/api/config': {
        target: 'http://localhost:3001',
        changeOrigin: true,
        secure: false,
      },
      // everything else under /api (auth, roster, etc.) goes to Spring
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },
    },
  },
});
