import '@testing-library/jest-dom/vitest'
import { cleanup } from '@testing-library/react'
import { afterEach, vi } from 'vitest'

// Nach jedem Test: gerenderte Komponenten entfernen und gefälschte Globals zurücksetzen.
afterEach(() => {
  cleanup()
  vi.unstubAllGlobals()
})
