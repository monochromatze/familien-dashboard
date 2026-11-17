import react from '@vitejs/plugin-react'
import { defineConfig } from 'vitest/config'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // Entwicklungsmodus: /api-Aufrufe gehen an das lokal laufende Backend (Port 8080).
    // Im Docker-Image liefert Spring Frontend und API gemeinsam aus, dort braucht es das nicht.
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
  test: {
    environment: 'jsdom',
    setupFiles: ['./src/setupTests.ts'],
  },
})
