package io.github.milczekt1.chassis.test.jdbc;

import java.lang.annotation.*;

/**
 * Clears the listed tables after each test method, once the test-managed transaction has ended.
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ClearTables {

    /**
     * Tables to be cleared. In {@link ClearMode#DELETE} mode they are cleared in this order, so list referencing
     * tables before the tables they reference.
     */
    String[] value();

    ClearMode mode() default ClearMode.DELETE;
}
