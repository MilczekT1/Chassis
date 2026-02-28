package io.github.milczekt1.chassis.testapp;

import io.github.milczekt1.chassis.observability.tracing.ChassisTraceResponseHeaderFilter;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static io.github.milczekt1.chassis.testapp.TracingDemoController.BASE_PATH;

/**
 * Controller demonstrating Chassis Observability tracing features.
 * <p>
 * This controller showcases:
 * <ul>
 *   <li><b>Response Header Exposure</b>: Trace-Id and Span-Id headers on every response</li>
 *   <li><b>Inbound Propagation</b>: Continuing incoming traces via W3C traceparent/tracestate</li>
 *   <li><b>Server Autoinstrumentation</b>: Automatic span creation for Spring MVC endpoints</li>
 *   <li><b>Client Autoinstrumentation</b>: Automatic trace propagation in RestTemplate calls</li>
 *   <li><b>Async Propagation</b>: Trace context preserved across @Async boundaries</li>
 *   <li><b>Custom Spans</b>: Manual span creation for business logic</li>
 * </ul>
 * </p>
 * <p>
 * OpenTelemetry Java Agent provides auto-instrumentation for:
 * - Spring MVC (server-side tracing)
 * - RestTemplate, WebClient, Feign (client-side tracing)
 * - Thread pools and @Async executors (async propagation)
 * - MongoDB, JDBC, Redis (database tracing)
 * </p>
 * <p>
 * Chassis Observability adds:
 * - Response header exposure (Trace-Id, Span-Id)
 * - Configuration-driven sampling
 * </p>
 *
 * @see ChassisTraceResponseHeaderFilter
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(BASE_PATH)
public class TracingDemoController {

    public static final String BASE_PATH = "/tracing";
    private static final String MESSAGE = "message";
    private static final String TRACE_ID = "traceId";
    private static final String NOTE = "note";

    private final TracingDemoService service;
    private final RestTemplate restTemplate;

    /**
     * Simple endpoint demonstrating response header exposure.
     * <p>
     * Response will include:
     * - Trace-Id: 128-bit trace identifier
     * - Span-Id: 64-bit span identifier
     * </p>
     */
    @GetMapping("/headers")
    public Map<String, String> headersExposure() {
        log.info("Processing simple trace request");

        // Get current span from OpenTelemetry context
        Span currentSpan = Span.current();
        String contextTraceId = currentSpan.getSpanContext().getTraceId();
        String contextSpanId = currentSpan.getSpanContext().getSpanId();

        return Map.of(
                MESSAGE, "Simple trace endpoint",
                TRACE_ID, contextTraceId,
                "spanId", contextSpanId,
                NOTE, "Check response headers for Trace-Id and Span-Id"
        );
    }

    /**
     * Endpoint demonstrating inbound trace propagation.
     * <p>
     * Accepts W3C traceparent header to continue existing trace:
     * <pre>
     * curl -H "traceparent: 00-{trace-id}-{parent-span-id}-01" http://localhost:8080/tracing/propagation
     * </pre>
     * </p>
     * <p>
     * If traceparent is provided, this request will become a child span
     * of the incoming trace instead of starting a new trace.
     * </p>
     *
     * @param traceparent W3C traceparent header (optional)
     * @return Response with trace propagation information
     */
    @GetMapping("/propagation")
    public Map<String, String> tracePropagation(
            @RequestHeader(value = "traceparent", required = false) String traceparent) {

        log.info("Processing trace propagation request with traceparent: {}", traceparent);
        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();
        return Map.of(
                MESSAGE, "Trace propagation endpoint",
                TRACE_ID, traceId,
                "traceparentReceived", traceparent != null ? traceparent : "none",
                NOTE, traceparent != null
                        ? "This span is a child of the incoming trace"
                        : "This is a new root trace"
        );
    }

    /**
     * Endpoint demonstrating client-side autoinstrumentation.
     * <p>
     * Makes an HTTP call using RestTemplate. The OpenTelemetry agent
     * automatically:
     * - Creates a client span for the outgoing request
     * - Propagates trace context via W3C traceparent header
     * - Links the client span to the current server span
     * </p>
     * <p>
     * This demonstrates distributed tracing across service boundaries.
     * </p>
     *
     * @return Response with client call information
     */
    @GetMapping("/client-call")
    public Map<String, String> clientCall() {
        log.info("Demonstrating client-side autoinstrumentation");

        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();

        try {
            // RestTemplate call is automatically instrumented by OpenTelemetry agent
            // A client span will be created and trace context propagated
            String response = restTemplate.getForObject("http://localhost:8080/dummy", String.class);

            return Map.of(
                    MESSAGE, "Client call completed",
                    TRACE_ID, traceId,
                    "remoteResponse", response,
                    NOTE, "Check Grafana for client span linked to this server span"
            );
        } catch (Exception e) {
            log.error("Client call failed", e);
            return Map.of(
                    MESSAGE, "Client call failed",
                    TRACE_ID, traceId,
                    "error", e.getMessage()
            );
        }
    }

    @GetMapping("/async")
    public Map<String, String> asyncPropagation() {
        log.info("Demonstrating async propagation");

        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();

        // Async method call - trace context automatically propagated
        service.asyncWork();
        return Map.of(
                MESSAGE, "Async operation initiated",
                TRACE_ID, traceId,
                NOTE, "Async work will appear as child span in same trace"
        );
    }

    @GetMapping("/custom-span")
    public Map<String, String> customSpan() {
        log.info("Demonstrating custom span creation");

        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();

        // Create custom span for business logic
        String result = service.businessLogicWithCustomSpan();

        return Map.of(
                MESSAGE, "Custom span demonstration",
                TRACE_ID, traceId,
                "businessResult", result,
                NOTE, "Check Grafana for custom 'business-logic' span"
        );
    }

    @GetMapping("/health")
    public Map<String, String> pathWhichShouldNotBeTraced() {
        log.debug("Health check");
        return Map.of("status", "UP");
    }
}

/**
 * Service demonstrating async propagation and custom spans.
 */
@Slf4j
@Service
class TracingDemoService {

    /**
     * Async method demonstrating trace context propagation.
     * <p>
     * The OpenTelemetry agent ensures the trace context from the
     * calling thread is propagated to this async executor.
     * </p>
     */
    @Async
    public CompletableFuture<String> asyncWork() {
        log.info("Executing async work in trace: {}",
                Span.current().getSpanContext().getTraceId());

        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return CompletableFuture.completedFuture("Async work completed");
    }

    /**
     * Business logic with custom span creation.
     * <p>
     * Demonstrates manual span creation using OpenTelemetry API
     * for fine-grained observability of business operations.
     * </p>
     */
    public String businessLogicWithCustomSpan() {
        // Get tracer from OpenTelemetry
        Tracer tracer = GlobalOpenTelemetry.getTracer("chassis-test-app");

        // Create custom span
        Scope scope = tracer.spanBuilder("business-logic")
                .setSpanKind(SpanKind.INTERNAL)
                .setAttribute("operation", "custom-business-logic")
                .startSpan()
                .makeCurrent();

        try {
            log.info("Executing business logic with custom span");

            // Simulate business logic
            Thread.sleep(50);

            return "Business logic executed";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Business logic interrupted";
        } finally {
            Span.current().end();
            scope.close();
        }
    }
}
