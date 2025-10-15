package io.github.milczekt1.chassis.test.clock;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Instant;

@FixedClock
@TestToolsIntegrationTest
class FixedClockIT {

    @Autowired
    Clock clock;

    @Test
    void givenClassLevelAnnotation_whenUseClock_thenReturnMockedDateTime() {
        // Given
        final var now = Instant.now(clock);
        Assertions.assertThat(now).isEqualTo(Instant.parse(FixedClock.DEFAULT_LOCAL_INSTANT));
    }

    @FixedClock("2027-01-10T10:00:00Z")
    @Test
    void givenMethodLevelAnnotation_whenUseClock_thenOverrideMockedDateTime() {
        // Given
        final var now = Instant.now(clock);
        Assertions.assertThat(now).isEqualTo(Instant.parse("2027-01-10T10:00:00Z"));
    }
}
