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

// Die Stufe (dev, qa, prod) kommt vom Backend, denn das Frontend ist in allen Stufen dasselbe Build.
// Schlägt der Aufruf fehl, zeigen wir einfach kein Banner.
async function loadEnv(): Promise<string | null> {
  try {
    const response = await fetch('/api/env')
    const body: unknown = await response.json()
    return typeof body === 'object' &&
      body !== null &&
      'env' in body &&
      typeof body.env === 'string'
      ? body.env
      : null
  } catch {
    return null
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
  const [env, setEnv] = useState<string | null>(null)

  useEffect(() => {
    let cancelled = false
    void loadHealth().then((next) => {
      if (!cancelled) {
        setState(next)
      }
    })
    void loadEnv().then((next) => {
      if (!cancelled) {
        setEnv(next)
      }
    })
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <main>
      {env === 'qa' && (
        <div role="note" style={{ background: '#ffd43b', padding: '0.5rem', fontWeight: 'bold' }}>
          QA
        </div>
      )}
      <h1>Familien-Dashboard</h1>
      <p>{statusText(state)}</p>
    </main>
  )
}

export default App
