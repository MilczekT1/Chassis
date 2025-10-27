package io.github.milczekt1.chassis.test.mongo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;
import java.util.function.Consumer;

import static io.github.milczekt1.chassis.test.TestContextUtils.getOptionalBean;
import static io.github.milczekt1.chassis.test.TestContextUtils.getTestClassAnnotation;

@Slf4j
public class MongoClearCollectionsListener extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext testContext) {
        var clearCollections = getTestClassAnnotation(testContext, ClearCollections.class);
        if (clearCollections.isEmpty()) {
            return;
        }
        ClearCollections annotation = clearCollections.get();
        getOptionalBean(testContext, MongoTemplate.class)
                .ifPresentOrElse(template -> {
                            var consumer = annotation.shouldDrop() ? new DropConsumer(template) : new ClearConsumer(template);
                            Arrays.stream(annotation.value()).forEach(consumer);
                        },
                        () -> log.warn("MongoTemplate bean not found. Skipping collections cleanup.")
                );
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }


    private static class DropConsumer implements Consumer<String> {
        private final MongoTemplate mongoTemplate;

        DropConsumer(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public void accept(String collection) {
            mongoTemplate.getCollection(collection).drop();
            log.info("Dropped {} collection", collection);
        }
    }

    private static class ClearConsumer implements Consumer<String> {
        private final MongoTemplate mongoTemplate;

        ClearConsumer(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        @Override
        public void accept(String collection) {
            var result = mongoTemplate.remove(new Query(), collection);
            log.info("Clearing up [{}] collections. Removed {} entries", collection, result.getDeletedCount());
        }
    }
}
