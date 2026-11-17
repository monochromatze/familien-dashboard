import { useEffect, useState } from 'react'

type Health = { status: string; db: string }

type HealthState =
  { kind: 'loading' } | { kind: 'loaded'; health: Health } | { kind: 'unreachable' }

function isHealth(value: unknown): value is Health {
  return (
    typeof value === 'object' &&
    value !== null &&
    'status' in value &&
    typeof value.status === 'string' &&
    'db' in value &&
    typeof value.db === 'string'
  )
}

// Auch bei HTTP 503 liefert das Backend einen lesbaren Status (degraded/down), den zeigen wir an.
async function loadHealth(): Promise<HealthState> {
  try {
    const response = await fetch('/api/health')
    const body: unknown = await response.json()
    return isHealth(body) ? { kind: 'loaded', health: body } : { kind: 'unreachable' }
  } catch {
    return { kind: 'unreachable' }
  }
}

function statusText(state: HealthState): string {
  switch (state.kind) {
    case 'loading':
      return 'Prüfe Verbindung …'
    case 'unreachable':
      return 'Backend nicht erreichbar'
    case 'loaded':
      return `Backend: ${state.health.status} · Datenbank: ${state.health.db}`
  }
}

function App() {
  const [state, setState] = useState<HealthState>({ kind: 'loading' })

  useEffect(() => {
    let cancelled = false
    void loadHealth().then((next) => {
      if (!cancelled) {
        setState(next)
      }
    })
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <main>
      <h1>Familien-Dashboard</h1>
      <p>{statusText(state)}</p>
    </main>
  )
}

export default App
