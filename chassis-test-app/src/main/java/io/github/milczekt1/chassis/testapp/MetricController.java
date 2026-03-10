package io.github.milczekt1.chassis.testapp;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static io.github.milczekt1.chassis.testapp.MetricController.BASE_PATH;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class MetricController {

    public static final String BASE_PATH = "/metrics";

    private final MeterRegistry registry;

    @GetMapping("/counter")
    public String counter() {
        log.info("counter endpoint hit.");
        registry.counter("counter.hit").increment();
        return "dummyResponseBody";
    }
}
