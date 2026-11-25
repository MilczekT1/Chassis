package io.github.milczekt1.chassis.test.jdbc;

import io.github.milczekt1.chassis.test.utils.TestToolsIntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@TestToolsIntegrationTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClearTablesIT {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeAll
    void createTables() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS orders (id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY)");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS order_items (
                    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    order_id bigint NOT NULL REFERENCES orders (id))""");
    }

    long insertOrderWithItem() {
        Long orderId = jdbcTemplate.queryForObject("INSERT INTO orders DEFAULT VALUES RETURNING id", Long.class);
        jdbcTemplate.update("INSERT INTO order_items (order_id) VALUES (?)", orderId);
        return orderId;
    }

    long countRows(String table) {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM " + table, Long.class);
    }

    void assertTablesAreEmpty() {
        assertThat(countRows("order_items")).isZero();
        assertThat(countRows("orders")).isZero();
    }

    @ClearTables({"order_items", "orders"})
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class DeleteMode {

        @Test
        void insertsOrderWithItem() {
            insertOrderWithItem();
        }

        @AfterAll
        void shouldDeleteRowsFromListedTables() {
            assertTablesAreEmpty();
        }
    }

    @ClearTables(value = {"orders", "order_items"}, mode = ClearMode.TRUNCATE)
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class TruncateMode {

        @Test
        @Order(1)
        void insertsOrderWithItem() {
            insertOrderWithItem();
        }

        @Test
        @Order(2)
        void shouldRestartIdentityOfTruncatedTables() {
            assertThat(insertOrderWithItem()).isEqualTo(1);
        }

        @AfterAll
        void shouldTruncateListedTablesInAnyOrder() {
            assertTablesAreEmpty();
        }
    }

    @ClearTables({"order_items", "orders"})
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    class InsideTestTransaction {

        @BeforeAll
        void commitOrderBeforeTestTransaction() {
            insertOrderWithItem();
        }

        @Test
        @Transactional
        void insertsOrderWithItem() {
            insertOrderWithItem();
        }

        @AfterAll
        void shouldDeleteRowsCommittedBeforeTestTransaction() {
            assertTablesAreEmpty();
        }
    }
}
