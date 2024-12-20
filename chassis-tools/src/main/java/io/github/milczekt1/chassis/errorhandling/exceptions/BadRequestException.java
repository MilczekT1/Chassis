package io.github.milczekt1.chassis.errorhandling.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
