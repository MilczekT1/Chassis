package io.github.milczekt1.chassis.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
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
            InternalServerErrorException.class,
            HashGenerationException.class
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
    public ResponseEntity<ErrorDescription> conflict(ResourceConflictException e){
        log.error(e.getMessage());
        return createResponseEntity(new ErrorDescription(HttpStatus.CONFLICT, e.getMessage()));
    }

    private ResponseEntity<ErrorDescription> createResponseEntity(ErrorDescription errorDescription){
        return ResponseEntity
                .status(errorDescription.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorDescription);
    }

    private ProblemDetail createProblemDetail(final HttpStatus httpStatus, RuntimeException e) {
        final var pd = ProblemDetail.forStatusAndDetail(httpStatus, e.getMessage());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
