package io.github.konradboniecki.chassis.exceptions;

public class InternalServerErrorException extends RuntimeException {

    public InternalServerErrorException(String message) {
        super(message);
        this.printStackTrace();
    }

    public InternalServerErrorException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
