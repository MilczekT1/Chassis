package pl.konradboniecki.chassis.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
