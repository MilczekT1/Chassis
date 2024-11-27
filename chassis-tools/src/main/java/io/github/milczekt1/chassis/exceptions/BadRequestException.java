package io.github.milczekt1.chassis.exceptions;

public class BadRequestException extends RuntimeException{

    public BadRequestException(String message) {
        super(message);
        this.printStackTrace();
    }

    public BadRequestException(String s, Throwable throwable) {
        super(s, throwable);
    }
}