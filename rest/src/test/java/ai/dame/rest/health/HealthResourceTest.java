package ai.dame.rest.health;

import static org.assertj.core.api.Assertions.assertThat;

import ai.dame.business.health.HealthService;
import org.junit.jupiter.api.Test;

class HealthResourceTest {

    @Test
    void healthReturnsServiceStatus() {
        HealthResource resource = new HealthResource();
        resource.healthService = new HealthService();

        assertThat(resource.health().status()).isEqualTo("UP");
    }
}
