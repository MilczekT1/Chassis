package io.github.milczekt1.chassis.test.jdbc;

public enum ClearMode {

    /**
     * {@code DELETE FROM} each table in the listed order. Identity columns keep counting.
     */
    DELETE,

    /**
     * One PostgreSQL {@code TRUNCATE ... RESTART IDENTITY} for all listed tables, in any order. A table referencing a
     * listed table must be listed too.
     */
    TRUNCATE
}
