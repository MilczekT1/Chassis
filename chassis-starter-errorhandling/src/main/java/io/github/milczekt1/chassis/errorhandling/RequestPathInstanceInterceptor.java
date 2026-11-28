package io.github.milczekt1.chassis.errorhandling;

import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpRequest;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.net.URI;

/**
 * Sets {@code instance} to the request path when the problem detail has none. Spring Framework 7.1+ leaves it unset.
 */
public class RequestPathInstanceInterceptor implements ErrorResponse.Interceptor {

    @Override
    public void handleError(ProblemDetail detail, @Nullable ErrorResponse errorResponse) {
        // Spring calls the overload below; without the request there is no path to set.
    }

    @Override
    public void handleError(ProblemDetail detail, @Nullable ErrorResponse errorResponse, HttpRequest request) {
        if (detail.getInstance() == null) {
            detail.setInstance(URI.create(request.getURI().getRawPath()));
        }
    }
}
