package io.github.milczekt1.chassis.exceptions;

public class ResourceCreationException extends BadRequestException {

    public ResourceCreationException(String message) {
        super(message);
    }

    public ResourceCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
