package io.github.milczekt1.chassis.test.mongo;

import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;

import static org.assertj.core.api.Assertions.assertThat;

@TestToolsIntegrationTest
class WithMongoIT {

    @Autowired
    MongoClient mongoClient;

    @Autowired
    MongoConnectionDetails mongoConnectionDetails;

    @Test
    void mongoClientConnectsToSharedContainer() {
        var clientHosts = mongoClient.getClusterDescription().getClusterSettings().getHosts().stream()
                .map(ServerAddress::toString)
                .toList();

        assertThat(clientHosts).isEqualTo(mongoConnectionDetails.getConnectionString().getHosts());
    }
}
