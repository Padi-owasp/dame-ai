package ai.dame.app;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

/** Black-box integration test verifying the health endpoint of the running Quarkus application. */
@QuarkusIntegrationTest
class HealthEndpointIT {

    @Test
    void healthEndpointReturnsUp() {
        given()
            .when().get("/api/health")
            .then()
            .statusCode(200)
            .body("status", is("UP"));
    }
}
