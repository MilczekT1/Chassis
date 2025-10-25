package io.github.milczekt1.chassis.test.mongo;

import com.mongodb.BasicDBObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
@ContextConfiguration(classes = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class
})
class MongoClearCollectionsListenerTest {

    public static final BasicDBObject ADDITIONAL_INDEX = new BasicDBObject("someIndexedField", 1);
    public static final String COLLECTION_NAME = "collection";

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ApplicationContext applicationContext;

    private final MongoClearCollectionsListener cleanUpListener = new MongoClearCollectionsListener();

    private TestContext mockTestClassContext(Object instance) {
        TestContext testContext = mock(TestContext.class);
        given(testContext.getTestClass()).willReturn((Class) instance.getClass());
        given(testContext.getApplicationContext()).willReturn(applicationContext);
        return testContext;
    }

    @BeforeEach
    public void setCollection() {
        mongoTemplate.insert(new BasicDBObject(), COLLECTION_NAME);
        mongoTemplate.getCollection(COLLECTION_NAME).createIndex(ADDITIONAL_INDEX);
    }

    @Test
    void shouldClearCollectionAndLeaveIndex() throws Exception {
        // given
        assertThat(mongoTemplate.count(new Query(), COLLECTION_NAME)).isEqualTo(1);
        // when
        WithClearCollectionClassAnnotation instance = new WithClearCollectionClassAnnotation();
        cleanUpListener.afterTestMethod(mockTestClassContext(instance));

        // then
        assertThat(mongoTemplate.count(new Query(), COLLECTION_NAME)).isZero();
        assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo()).hasSize(2);
        assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo().get(0).getName()).isEqualTo(
                "_id_");
        assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo().get(1).getName()).isEqualTo(
                "someIndexedField_1");
    }

    @Test
    void shouldDropCollectionAndIndex() throws Exception {
        WithClearDropCollectionClassAnnotation instance = new WithClearDropCollectionClassAnnotation();
        cleanUpListener.afterTestMethod(mockTestClassContext(instance));
        assertThat(mongoTemplate.count(new Query(), COLLECTION_NAME)).isZero();
        assertThat(mongoTemplate.indexOps(COLLECTION_NAME).getIndexInfo()).isEmpty();
    }

    @ClearCollections(COLLECTION_NAME)
    static class WithClearCollectionClassAnnotation {
    }

    @ClearCollections(value = COLLECTION_NAME, shouldDrop = true)
    static class WithClearDropCollectionClassAnnotation {
    }

}
