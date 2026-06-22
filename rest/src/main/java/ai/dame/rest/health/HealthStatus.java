package ai.dame.rest.health;

/**
 * Health response payload.
 *
 * @param status health token, e.g. {@code "UP"}.
 */
public record HealthStatus(String status) {}
