package pl.konradboniecki.chassis.configuration.tracing;

import brave.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DistributedTracingProperties.class)
public class DistributedTracingAutoConfiguration {

    @Autowired
    private DistributedTracingProperties distributedTracingProperties;

    @Bean
    HttpResponseTraceIdInjector httpResponseTraceIdInjector() {
        return HttpResponseTraceIdInjector.builder()
                .traceIdKey(distributedTracingProperties.getTraceIdKey())
                .spanIdKey(distributedTracingProperties.getSpanIdKey())
                .build();
    }

    @Bean
    HttpResponseTraceIdInjectorFilter responseTraceIdInjectorFilter(Tracer tracer, HttpResponseTraceIdInjector httpResponseTraceIdInjector) {
        return new HttpResponseTraceIdInjectorFilter(tracer, httpResponseTraceIdInjector);
    }
}
