package io.github.milczekt1.chassis.tools;

import io.github.milczekt1.chassis.exceptions.InternalServerErrorException;

public class HashGenerationException extends InternalServerErrorException {

    public HashGenerationException(String s, Throwable throwable) {
         super(s, throwable);
    }
}
