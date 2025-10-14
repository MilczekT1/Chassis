package io.github.milczekt1.chassis.test.clock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

import static io.github.milczekt1.chassis.test.TestContextUtils.*;
import static org.mockito.Mockito.when;

@Slf4j
public class FixedClockListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestMethod(TestContext testContext) {
        var fixedClock = getMethodAnnotation(testContext, FixedClock.class)
                .or(() -> getTestClassAnnotation(testContext, FixedClock.class));
        if (fixedClock.isEmpty()) {
            log.trace("@FixedClock not found.");
            return;
        }
        mockClock(testContext, fixedClock.get());
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        if (getMethodAnnotation(testContext, FixedClock.class).isEmpty()) {
            log.trace("@FixedClock not found on method.");
            return;
        }

        var fixedClock = getTestClassAnnotation(testContext, FixedClock.class);
        if (fixedClock.isEmpty()) {
            // Do not reset  mock if annotation is missing.
            return;
        }
        mockClock(testContext, fixedClock.get());
    }

    private void mockClock(TestContext testContext, FixedClock fixedClock) {
        log.info("@FixedClock found. Mocking clock to return {}", fixedClock.value());
        var instant = Instant.parse(fixedClock.value());
        var mockedClock = getBean(testContext, Clock.class);
        when(mockedClock.instant()).thenReturn(instant);
        when(mockedClock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }
}
