package pl.konradboniecki.chassis.configuration.tracing;

import brave.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class HttpResponseTraceIdInjectorFilter extends GenericFilterBean {

    private final Tracer tracer;
    private final HttpResponseTraceIdInjector httpResponseTraceIdInjector;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var servletResponse = (HttpServletResponse) response;
        var traceContext = this.tracer.currentSpan().context();
        httpResponseTraceIdInjector.inject(traceContext, servletResponse);
        chain.doFilter(request, servletResponse);
    }
}
