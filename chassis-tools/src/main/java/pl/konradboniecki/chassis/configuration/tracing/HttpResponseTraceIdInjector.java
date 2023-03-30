package pl.konradboniecki.chassis.configuration.tracing;

import brave.propagation.TraceContext;
import brave.propagation.TraceContext.Injector;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;

@Builder
public class HttpResponseTraceIdInjector implements Injector<HttpServletResponse> {

    private final String traceIdKey;
    private final String spanIdKey;

    @Override
    public void inject(TraceContext traceContext, HttpServletResponse response) {
        response.addHeader(traceIdKey, traceContext.traceIdString());
        response.addHeader(spanIdKey, traceContext.spanIdString());
    }
}
