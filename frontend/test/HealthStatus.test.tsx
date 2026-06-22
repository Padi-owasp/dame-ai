import { render, screen } from '@testing-library/react'
import { beforeEach, expect, test, vi } from 'vitest'
import HealthStatus from '../src/HealthStatus'

beforeEach(() => {
  vi.stubGlobal('fetch', vi.fn().mockResolvedValue({
    ok: true,
    json: async () => ({ status: 'UP' }),
  }))
})

test('renders the backend status returned by /api/health', async () => {
  render(<HealthStatus />)
  expect(await screen.findByText('Backend status: UP')).toBeInTheDocument()
})
