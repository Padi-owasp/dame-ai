package ai.dame.business.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthServiceTest {

    @Test
    void statusIsUp() {
        assertThat(new HealthService().status()).isEqualTo("UP");
    }
}
