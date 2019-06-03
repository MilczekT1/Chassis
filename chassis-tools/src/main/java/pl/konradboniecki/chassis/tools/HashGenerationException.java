package pl.konradboniecki.chassis.tools;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

//TODO: test response status and message
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
class HashGenerationException extends RuntimeException {

    public HashGenerationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}