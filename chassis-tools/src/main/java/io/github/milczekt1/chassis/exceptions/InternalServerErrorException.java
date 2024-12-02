package io.github.milczekt1.chassis.exceptions;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
    }

    public InternalServerErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
