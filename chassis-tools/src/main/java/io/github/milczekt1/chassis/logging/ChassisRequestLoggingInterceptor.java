package io.github.milczekt1.chassis.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class ChassisRequestLoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        req.setAttribute("startNanos", System.nanoTime());
        log.info("Incoming: method: {}, uri: {}, queryString: ?{}, remoteIp: {}, userAgent: {}",
                req.getMethod(), req.getRequestURI(), req.getQueryString(),
                req.getRemoteAddr(), req.getHeader("User-Agent"));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res,
                                Object handler, Exception ex) {
        long tookMs = (System.nanoTime() - (long) req.getAttribute("startNanos")) / 1_000_000;
        log.info("Request completed. Status: {}, duration: {}ms.", res.getStatus(), tookMs);
    }
}
