package io.github.milczekt1.chassis.errorhandling.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
