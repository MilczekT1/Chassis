package io.github.milczekt1.chassis.testapp.utils.containers;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class MongoContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        TestContainerDefinitions.MONGODB.start();
        TestPropertyValues.of(
                "spring.data.mongodb.uri=" + TestContainerDefinitions.MONGODB.getConnectionString(),
                "spring.data.mongodb.database=" + TestContainerDefinitions.MONGODB_DB_NAME
        ).applyTo(configurableApplicationContext.getEnvironment());
    }
}
