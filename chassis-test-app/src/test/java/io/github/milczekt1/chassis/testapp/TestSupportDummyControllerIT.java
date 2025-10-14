package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.test.clock.FixedClock;
import io.github.milczekt1.chassis.test.controller.ControllerTest;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.Instant;

@FixedClock
@ControllerTest(DummyController.class)
@ExtendWith(SpringExtension.class)
class TestSupportDummyControllerIT {

    @Autowired
    Clock clock;

    @Test
    void fixedClock() {
        // Given
        final var now = Instant.now(clock);
        Assertions.assertThat(now).isEqualTo(Instant.parse(FixedClock.DEFAULT_LOCAL_INSTANT));
    }

    @Test
    void controllerTestAnnotation() {
        RestAssuredMockMvc.given()
                .when()
                .get()
                .then()
                .statusCode(200)
                .and().extract().body().equals("dummy");
    }

}
