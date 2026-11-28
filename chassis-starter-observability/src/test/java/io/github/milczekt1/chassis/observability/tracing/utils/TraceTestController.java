package io.github.milczekt1.chassis.observability.tracing.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/test/tracing")
public class TraceTestController {

    public static final String PRESET_INSTANCE = "/orders/42/errors/1";

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/problem")
    public ProblemDetail problem() {
        return ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/problem-entity")
    public ResponseEntity<ProblemDetail> problemEntity() {
        return ResponseEntity.of(ProblemDetail.forStatus(HttpStatus.CONFLICT)).build();
    }

    @GetMapping("/problem-with-instance")
    public ProblemDetail problemWithInstance() {
        ProblemDetail detail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        detail.setInstance(URI.create(PRESET_INSTANCE));
        return detail;
    }
}
