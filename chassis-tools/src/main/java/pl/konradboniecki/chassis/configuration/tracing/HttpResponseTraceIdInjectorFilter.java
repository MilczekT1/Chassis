package pl.konradboniecki.chassis.configuration.tracing;

import brave.Tracer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
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
