package io.github.milczekt1.chassis.errorhandling;

import lombok.Getter;

@Getter
public class Violation {

    private final String field;
    private final String message;

    public Violation(final String field, final String message) {
        this.field = field;
        this.message = message;
    }
}
