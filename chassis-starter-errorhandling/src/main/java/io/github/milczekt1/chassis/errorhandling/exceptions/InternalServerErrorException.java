package io.github.milczekt1.chassis.errorhandling.exceptions;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
