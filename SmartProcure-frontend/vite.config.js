import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// Helper function to bypass proxy for HTML document navigation requests (deep links, browser address bar, email links)
const bypassHtml = (req) => {
  if (req.headers.accept && req.headers.accept.includes('html')) {
    return '/index.html';
  }
};

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/users': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/companies': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/procurement-cases': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/vendor-quotes': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/approvals': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
      '/approval-matrices': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        bypass: bypassHtml,
      },
    },
  },
});
