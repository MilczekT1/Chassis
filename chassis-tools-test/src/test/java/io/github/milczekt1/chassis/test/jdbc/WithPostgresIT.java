package io.github.milczekt1.chassis.test.jdbc;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@TestToolsIntegrationTest
class WithPostgresIT {

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcConnectionDetails jdbcConnectionDetails;

    @Test
    void dataSourceConnectsToSharedContainer() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL()).isEqualTo(jdbcConnectionDetails.getJdbcUrl());
        }
    }
}
