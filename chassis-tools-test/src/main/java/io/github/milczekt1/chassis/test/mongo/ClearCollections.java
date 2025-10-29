package io.github.milczekt1.chassis.test.mongo;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ClearCollections {

    /**
     * Collections to be cleared or dropped.
     */
    String[] value();

    boolean shouldDrop() default false;
}
