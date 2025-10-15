package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.test.clock.FixedClock;
import io.github.milczekt1.chassis.test.controller.ControllerTest;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

@FixedClock
@ControllerTest(value = DummyController.class, basePath = DummyController.BASE_PATH)
class TestSupportDummyControllerIT {

    @Autowired
    Clock clock;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void fixedClock() {
        // Given
        final var now = Instant.now(clock);
        assertThat(now).isEqualTo(Instant.parse(FixedClock.DEFAULT_LOCAL_INSTANT));
    }

    @Test
    void controllerTestAnnotation_SetBasePath() {
        assertThat(RestAssuredMockMvc.basePath).isEqualTo(DummyController.BASE_PATH);
    }

    @Test
    void controllerTestAnnotation_SetDefaultCharsetForJson() {
        final var defaultCharsetForJson = RestAssuredMockMvc.config()
                .getEncoderConfig().defaultCharsetForContentType(ContentType.JSON);
        assertThat(defaultCharsetForJson).isEqualTo("UTF-8");
    }

    @Test
    void controllerTestAnnotation_example() {
        RestAssuredMockMvc.given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .body(equalTo("dummyResponseBody"));
    }

}
