package io.github.milczekt1.chassis.errorhandling;

import io.github.milczekt1.chassis.errorhandling.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@Slf4j
@ControllerAdvice
public class ChassisExceptionHandler extends ResponseEntityExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            ResourceCreationException.class,
            NumberFormatException.class,
            IllegalArgumentException.class
    })
    public ProblemDetail badRequest(RuntimeException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            InternalServerErrorException.class
    })
    public ProblemDetail internalServerError(RuntimeException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail notFound(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, e);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail conflict(ResourceConflictException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, e);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        final ProblemDetail problemDetail = createProblemDetail(e, BAD_REQUEST, e.getMessage(), null, null, request);
        final List<Violation> violations = fromBindingResult(e.getBindingResult());
        final ProblemDetail violationProblem = asViolationProblem(problemDetail, violations);
        return handleExceptionInternal(e, violationProblem, headers, valueOf(violationProblem.getStatus()), request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(final ConstraintViolationException e) {
        final ProblemDetail problemDetail = createProblemDetail(BAD_REQUEST, e);
        final List<Violation> violations = fromConstraintViolations(e.getConstraintViolations());
        final ProblemDetail violationProblem = asViolationProblem(problemDetail, violations);
        return violationProblem;
    }

    private ProblemDetail createProblemDetailsWithViolations(final HttpStatus httpStatus, final RuntimeException e, List<Violation> violations) {
        final var pd = createProblemDetail(httpStatus, e);
        pd.setProperty("violations", violations);
        return pd;
    }

    private ProblemDetail asViolationProblem(final ProblemDetail pd, final List<Violation> violations) {
        pd.setDetail("Constraint Violation");
        pd.setProperty("violations", violations);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    private ProblemDetail createProblemDetail(final HttpStatus httpStatus, final RuntimeException e) {
        final var pd = ProblemDetail.forStatusAndDetail(httpStatus, e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
