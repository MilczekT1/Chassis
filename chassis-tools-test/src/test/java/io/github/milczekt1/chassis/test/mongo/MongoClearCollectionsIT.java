package io.github.milczekt1.chassis.test.mongo;

import com.mongodb.BasicDBObject;
import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;

@TestToolsIntegrationTest
public class MongoClearCollectionsIT {

    public static final BasicDBObject ADDITIONAL_INDEX = new BasicDBObject("someIndexedField", 1);
    public static final String COLLECTION_NAME = "collection";

    @Autowired
    MongoTemplate mongoTemplate;

    public void setupData() {
        mongoTemplate.insert(new BasicDBObject(), COLLECTION_NAME);
        mongoTemplate.getCollection(COLLECTION_NAME).createIndex(ADDITIONAL_INDEX);
    }

    @ClearCollections(COLLECTION_NAME)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class WithoutDrop {

        @Test
        void checkAfterAllAssertions() {
            setupData();
        }

        @AfterAll
        void shouldClearCollectionAndLeaveIndex() {
            assertThat(mongoTemplate.count(new Query(), COLLECTION_NAME)).isZero();
            assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo()).hasSize(2);
            assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo().get(0).getName()).isEqualTo(
                    "_id_");
            assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo().get(1).getName()).isEqualTo(
                    "someIndexedField_1");
        }
    }

    @ClearCollections(value = COLLECTION_NAME, shouldDrop = true)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class WithDrop {

        @Test
        void checkAfterAllAssertions() {
            setupData();
        }

        @AfterAll
        void shouldDropCollectionAndIndex() throws Exception {
            assertThat(mongoTemplate.count(new Query(), COLLECTION_NAME)).isZero();
            assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo()).isEmpty();
        }
    }
}
