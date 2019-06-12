package pl.konradboniecki.chassis.exceptions;

public class HashGenerationException extends RuntimeException {

    public HashGenerationException(String s, Throwable throwable) {
         super(s, throwable);
    }
}