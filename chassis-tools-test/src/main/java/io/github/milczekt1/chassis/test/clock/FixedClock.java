package io.github.milczekt1.chassis.test.clock;

import org.springframework.test.context.bean.override.mockito.MockReset;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.lang.annotation.*;
import java.time.Clock;

@Documented
@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MockitoBean(types = Clock.class, reset = MockReset.NONE)
public @interface FixedClock {

    String DEFAULT_LOCAL_INSTANT = "2026-01-10T10:00:00Z";

    String value() default DEFAULT_LOCAL_INSTANT;
}
