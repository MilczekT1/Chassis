package io.github.milczekt1.chassis.errorhandling.exceptions;

public class ResourceConflictException extends RuntimeException {

    public ResourceConflictException(String message) {
        super(message);
    }

    public ResourceConflictException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
