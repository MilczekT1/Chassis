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
    @ExceptionHandler(HashGenerationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDescription> jarNotFoundException(HashGenerationException e){
        log.error(e.getMessage());
        final ErrorDescription errorDescription =
                new ErrorDescription(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());

        return ResponseEntity
                .status(errorDescription.getStatus())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .body(errorDescription);
    }
}
