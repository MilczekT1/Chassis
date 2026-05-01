package io.github.milczekt1.chassis.errorhandling.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;

public class ValidableObject {

    @Min(value = 18)
    public Long age;

    @JsonCreator
    public ValidableObject(
            @JsonProperty("age") final Long age
    ) {
        this.age = age;
    }
}
