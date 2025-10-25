package io.github.milczekt1.chassis.test.mongo;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

@AutoConfiguration
@ConditionalOnClass(MongoTemplate.class)
public class ChassisTestMongoCleanupConfiguration {

    @Bean
    @ConditionalOnBean(MongoTemplate.class)
    public MongoClearCollectionsListener mongoClearCollectionsListener() {
        return new MongoClearCollectionsListener();
    }
}
