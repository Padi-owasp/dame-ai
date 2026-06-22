package ai.dame.business.health;

import jakarta.enterprise.context.ApplicationScoped;

/** Provides the application's health status for monitoring and the UI smoke check. */
@ApplicationScoped
public class HealthService {

    /**
     * Returns the current health token.
     *
     * @return {@code "UP"} when the service is operational.
     */
    public String status() {
        return "UP";
    }
}
