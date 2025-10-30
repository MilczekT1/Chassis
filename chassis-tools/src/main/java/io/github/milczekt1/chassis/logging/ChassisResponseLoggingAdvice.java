package io.github.milczekt1.chassis.logging;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Collection;

@Slf4j
@ControllerAdvice
public class ChassisResponseLoggingAdvice implements ResponseBodyAdvice<Object> {
//    private static final Logger log = LoggerFactory.getLogger(ResponseLoggingAdvice.class);

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> converterType,
                                  ServerHttpRequest req, ServerHttpResponse res) {
        HttpServletResponse servletRes =
                ((ServletServerHttpResponse) res).getServletResponse();
        if (body instanceof Collection<?> c) {
            log.info("Collection returned on endpoint: {}, size: {}, status: {}", req.getURI().getPath(), c.size(),
                    servletRes.getStatus());
        }
        return body;
    }
}
