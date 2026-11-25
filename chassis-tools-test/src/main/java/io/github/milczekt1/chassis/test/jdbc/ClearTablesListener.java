package io.github.milczekt1.chassis.test.jdbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.sql.DataSource;

import static io.github.milczekt1.chassis.test.TestContextUtils.getOptionalBean;
import static io.github.milczekt1.chassis.test.TestContextUtils.getTestClassAnnotation;

@Slf4j
public class ClearTablesListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) {
        getTestClassAnnotation(testContext, ClearTables.class).ifPresent(clearTables ->
                getOptionalBean(testContext, DataSource.class).ifPresentOrElse(
                        dataSource -> clear(new JdbcTemplate(dataSource), clearTables),
                        () -> log.warn("DataSource bean not found. Skipping tables cleanup.")
                )
        );
    }

    /**
     * Do not lower this order: after-test callbacks run in reverse order, and a cleanup that runs before
     * TransactionalTestExecutionListener joins the test-managed transaction and is silently rolled back with it.
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

    private static void clear(JdbcTemplate jdbcTemplate, ClearTables clearTables) {
        switch (clearTables.mode()) {
            case DELETE -> JdbcTestUtils.deleteFromTables(jdbcTemplate, clearTables.value());
            case TRUNCATE -> truncate(jdbcTemplate, clearTables.value());
        }
    }

    // Table names come from the test class's @ClearTables, never from runtime input.
    @SuppressWarnings("java:S2077")
    private static void truncate(JdbcTemplate jdbcTemplate, String[] tableNames) {
        var tables = String.join(", ", tableNames);
        jdbcTemplate.execute("TRUNCATE TABLE " + tables + " RESTART IDENTITY");
        log.info("Truncated [{}] tables", tables);
    }
}
