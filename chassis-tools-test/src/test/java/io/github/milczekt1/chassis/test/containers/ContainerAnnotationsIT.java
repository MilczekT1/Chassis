package io.github.milczekt1.chassis.test.containers;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.autoconfigure.JdbcConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@TestToolsIntegrationTest
class ContainerAnnotationsIT {

    @Autowired
    MongoClient mongoClient;

    @Autowired
    MongoConnectionDetails mongoConnectionDetails;

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcConnectionDetails jdbcConnectionDetails;

    @Test
    void mongoClientConnectsToSharedContainer() {
        var clientHosts = mongoClient.getClusterDescription().getClusterSettings().getHosts().stream()
                .map(ServerAddress::toString)
                .toList();

        assertThat(clientHosts).isEqualTo(mongoConnectionDetails.getConnectionString().getHosts());
    }

    @Test
    void dataSourceConnectsToSharedContainer() throws SQLException {
        try (var connection = dataSource.getConnection()) {
            assertThat(connection.getMetaData().getURL()).isEqualTo(jdbcConnectionDetails.getJdbcUrl());
        }
    }
}
