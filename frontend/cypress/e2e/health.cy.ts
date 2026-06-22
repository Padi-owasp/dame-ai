describe('health vertical slice', () => {
  it('loads the app and shows the backend status from /api/health', () => {
    cy.visit('/')
    cy.contains('h1', 'dame-ai')
    cy.contains('Backend status: UP')
  })
})
