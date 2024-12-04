package io.github.milczekt1.chassis.errorhandling;

import io.github.milczekt1.chassis.errorhandling.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.List;

import static io.github.milczekt1.chassis.errorhandling.ViolationCreator.fromBindingResult;
import static io.github.milczekt1.chassis.errorhandling.ViolationCreator.fromConstraintViolations;
import static org.springframework.http.HttpStatus.*;

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
    public ProblemDetail handleBadRequest(RuntimeException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.BAD_REQUEST, e);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            InternalServerErrorException.class
    })
    public ProblemDetail handleInternalServerError(RuntimeException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, e);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.NOT_FOUND, e);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException.class)
    public ProblemDetail handleConflict(ResourceConflictException e) {
        log.error(e.getMessage());
        return createProblemDetail(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleDefaultException(final Exception e) {
        log.error(e.getMessage());
        return createProblemDetail(INTERNAL_SERVER_ERROR, e);
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
        return asViolationProblem(problemDetail, violations);
    }

    private ProblemDetail asViolationProblem(final ProblemDetail pd, final List<Violation> violations) {
        pd.setDetail("Constraint Violation");
        pd.setProperty("violations", violations);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    private ProblemDetail createProblemDetail(final HttpStatus httpStatus, final Exception e) {
        final var pd = ProblemDetail.forStatusAndDetail(httpStatus, e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
