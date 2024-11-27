package io.github.konradboniecki.chassis.exceptions;

public class ResourceCreationException extends RuntimeException {
    public ResourceCreationException(String message) {
        super(message);
    }

    public ResourceCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
