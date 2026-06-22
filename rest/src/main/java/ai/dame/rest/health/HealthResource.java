package ai.dame.rest.health;

import ai.dame.business.health.HealthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/** REST endpoint exposing the application health status under {@code /api/health}. */
@Path("/api/health")
public class HealthResource {

    /** Domain service providing the status token. */
    @Inject
    HealthService healthService;

    /**
     * Reports application health.
     *
     * @return a {@link HealthStatus} serialized as {@code {"status":"UP"}}.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public HealthStatus health() {
        return new HealthStatus(healthService.status());
    }
}
