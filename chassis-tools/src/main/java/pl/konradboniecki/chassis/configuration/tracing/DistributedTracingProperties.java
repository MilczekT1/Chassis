package pl.konradboniecki.chassis.configuration.tracing;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "budget.chassis.tracing")
public class DistributedTracingProperties {

    private String traceIdKey = "X-TraceId";
    private String spanIdKey = "X-SpanId";
}
