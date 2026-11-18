import { render, screen } from '@testing-library/react'
import { describe, expect, it, vi } from 'vitest'
import App from './App.tsx'

function jsonResponse(body: unknown, status: number): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { 'Content-Type': 'application/json' },
  })
}

function stubFetch() {
  const fetchMock = vi.fn<typeof fetch>()
  vi.stubGlobal('fetch', fetchMock)
  return fetchMock
}

// Antwortet je nach Adresse: /api/env mit der Stufe, alles andere mit gesundem Health-Status.
function stubStage(env: string) {
  const fetchMock = stubFetch()
  fetchMock.mockImplementation((input) =>
    Promise.resolve(
      String(input) === '/api/env'
        ? jsonResponse({ env }, 200)
        : jsonResponse({ status: 'ok', db: 'ok' }, 200),
    ),
  )
}

describe('Stufen-Banner', () => {
  it('zeigt das Banner "QA", wenn das Backend die Stufe qa meldet', async () => {
    stubStage('qa')

    render(<App />)

    expect(await screen.findByText('QA')).toBeInTheDocument()
  })

  it('zeigt kein Banner auf prod', async () => {
    stubStage('prod')

    render(<App />)

    expect(await screen.findByText('Backend: ok · Datenbank: ok')).toBeInTheDocument()
    expect(screen.queryByText('QA')).not.toBeInTheDocument()
  })
})

describe('App', () => {
  it('zeigt Backend ok und Datenbank ok, wenn /api/health ok meldet', async () => {
    const fetchMock = stubFetch()
    fetchMock.mockResolvedValue(jsonResponse({ status: 'ok', db: 'ok' }, 200))

    render(<App />)

    expect(await screen.findByText('Backend: ok · Datenbank: ok')).toBeInTheDocument()
    expect(fetchMock).toHaveBeenCalledWith('/api/health')
  })

  it('zeigt den Zustand der Datenbank, wenn das Backend 503 degraded meldet', async () => {
    const fetchMock = stubFetch()
    fetchMock.mockResolvedValue(jsonResponse({ status: 'degraded', db: 'down' }, 503))

    render(<App />)

    expect(await screen.findByText('Backend: degraded · Datenbank: down')).toBeInTheDocument()
  })

  it('zeigt "Backend nicht erreichbar", wenn der Aufruf scheitert', async () => {
    const fetchMock = stubFetch()
    fetchMock.mockRejectedValue(new TypeError('Failed to fetch'))

    render(<App />)

    expect(await screen.findByText('Backend nicht erreichbar')).toBeInTheDocument()
  })
})
