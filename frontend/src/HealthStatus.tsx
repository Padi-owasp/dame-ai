import { useEffect, useState } from 'react'

/** Fetches the backend health status from /api/health and displays it. */
export default function HealthStatus() {
  const [status, setStatus] = useState<string>('…')

  useEffect(() => {
    fetch('/api/health')
      .then((response) => response.json())
      .then((data: { status: string }) => setStatus(data.status))
      .catch(() => setStatus('unreachable'))
  }, [])

  return <p>Backend status: {status}</p>
}
