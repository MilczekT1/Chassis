package io.github.milczekt1.chassis.test.containers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ApplicationContext;

import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class PostgresContainerConfigurationIT {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(PostgresContainerConfiguration.class);

    @Test
    void sharesOneContainerAcrossContexts() {
        contextRunner.run(first -> contextRunner.run(second ->
                assertThat(jdbcUrl(second)).isEqualTo(jdbcUrl(first))));
    }

    @Test
    void keepsContainerRunningWhenAnotherContextCloses() {
        contextRunner.run(first -> {
            contextRunner.run(second -> assertThat(selectOne(second)).isEqualTo(1));
            assertThat(selectOne(first)).isEqualTo(1);
        });
    }

    @Test
    void createsContainerFromImageProperty() {
        contextRunner.run(defaultImage -> contextRunner
                .withPropertyValues("chassis.test.postgres.image=example/not-postgres:1")
                .run(overridden -> assertThat(overridden).getFailure()
                        .rootCause()
                        .hasMessageContaining("example/not-postgres:1")));
    }

    private static String jdbcUrl(ApplicationContext context) {
        return context.getBean(JdbcConnectionDetails.class).getJdbcUrl();
    }

    private static int selectOne(ApplicationContext context) throws SQLException {
        var details = context.getBean(JdbcConnectionDetails.class);
        try (var connection = DriverManager.getConnection(details.getJdbcUrl(), details.getUsername(), details.getPassword());
             var resultSet = connection.createStatement().executeQuery("SELECT 1")) {
            resultSet.next();
            return resultSet.getInt(1);
        }
    }
}
