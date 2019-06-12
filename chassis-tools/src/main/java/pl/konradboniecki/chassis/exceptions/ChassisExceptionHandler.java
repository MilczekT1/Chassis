package pl.konradboniecki.chassis.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.konradboniecki.chassis.ErrorDescription;

@Slf4j
@ControllerAdvice
public class ChassisExceptionHandler extends ResponseEntityExceptionHandler {

    //TODO: test it
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            BadRequestException.class,
            ResourceCreationException.class
    })
    public ResponseEntity<ErrorDescription> badRequest(RuntimeException e){
        log.error(e.getMessage());
        final ErrorDescription errorDescription =
                new ErrorDescription(HttpStatus.BAD_REQUEST, e.getMessage());

        return createResponseEntity(errorDescription);
    }
    //TODO: test it
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            InternalServerErrorException.class,
            HashGenerationException.class
    })
    public ResponseEntity<ErrorDescription> internalServerError(RuntimeException e){
        log.error(e.getMessage());
        final ErrorDescription errorDescription =
                new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        return createResponseEntity(errorDescription);
    }

    //TODO: test it
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDescription> notFound(ResourceNotFoundException e) {
        log.error(e.getMessage());
        final ErrorDescription errorDescription =
                new ErrorDescription(HttpStatus.NOT_FOUND, e.getMessage());

        return createResponseEntity(errorDescription);
    }
    //TODO: test it
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorDescription> conflict(ResourceConflictException e){
        log.error(e.getMessage());
        final ErrorDescription errorDescription =
                new ErrorDescription(HttpStatus.CONFLICT, e.getMessage());

        return createResponseEntity(errorDescription);
    }

    private ResponseEntity<ErrorDescription> createResponseEntity(ErrorDescription errorDescription){
        return ResponseEntity
                .status(errorDescription.getStatus())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(errorDescription);
    }
}
