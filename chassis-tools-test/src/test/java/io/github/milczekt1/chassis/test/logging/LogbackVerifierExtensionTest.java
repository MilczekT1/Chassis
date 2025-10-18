package io.github.milczekt1.chassis.test.logging;

import ch.qos.logback.classic.Level;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(LogbackVerifierExtension.class)
class LogbackVerifierExtensionTest {

    private static final String TEST_MESSAGE = "testMessage";
    Logger log = LoggerFactory.getLogger(LogbackVerifierExtensionTest.class);
    Logger thirdPartyLog = LoggerFactory.getLogger("third.party.LogbackVerifierExtensionTest");


    @Test
    void shouldExcludeNonCustomLogs(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(thirdPartyLog);

        // when then
        assertThatCode(() -> {
            logVerifier.hasNoTrace();
            logVerifier.hasNoDebug();
            logVerifier.hasNoInfo();
            logVerifier.hasNoWarnings();
            logVerifier.hasNoErrors();
        }).doesNotThrowAnyException();
    }

    @Test
    void hasNoMethodsShouldFailAssertion(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(log);
        // when then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoTrace);
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoDebug);
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoInfo);
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoWarnings);
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoErrors);
    }

    @Test
    void shouldIncludeCustomLogs(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(log);

        // when then
        assertThatCode(() -> {
            logVerifier.containsTrace(TEST_MESSAGE);
            logVerifier.containsDebug(TEST_MESSAGE);
            logVerifier.containsInfo(TEST_MESSAGE);
            logVerifier.containsWarn(TEST_MESSAGE);
            logVerifier.containsError(TEST_MESSAGE);
        }).doesNotThrowAnyException();
    }

    @Test
    void containsMethodsShouldFailAssertion(LogVerifier logVerifier) {
        // given
        // when then
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> logVerifier.containsTrace(TEST_MESSAGE));
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> logVerifier.containsDebug(TEST_MESSAGE));
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> logVerifier.containsInfo(TEST_MESSAGE));
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> logVerifier.containsWarn(TEST_MESSAGE));
        // and
        assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(() -> logVerifier.containsError(TEST_MESSAGE));
    }

    @Test
    void getAllLoggedEventsShouldWork(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(log);
        // when then
        final var loggedEvents = logVerifier.getAllLoggedEvents();
        assertThat(loggedEvents)
                .isNotNull()
                .hasSize(5);
    }

    @Test
    void containsShouldWork(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(log);
        // when then
        final var loggedEvents = logVerifier.contains(TEST_MESSAGE, Level.INFO);
        assertThat(loggedEvents).isNotNull();
        loggedEvents.hasSize(5, log.getName());
    }

    @Test
    void hasSizeShouldWork(LogVerifier logVerifier) {
        // given
        logOnEveryLevel(log);
        // when then
        assertThatCode(() -> {
            logVerifier.hasSize(5, log.getName());
            // and
            logVerifier.hasSize(5);
            // and
            logVerifier.hasSize(1, Level.TRACE);
            logVerifier.hasSize(1, Level.DEBUG);
            logVerifier.hasSize(1, Level.INFO);
            logVerifier.hasSize(1, Level.WARN);
            logVerifier.hasSize(1, Level.ERROR);
        }).doesNotThrowAnyException();
    }

    @Test
    void hasNoEntriesShouldWork(LogVerifier logVerifier) {
        // when
        assertThatCode(logVerifier::hasNoEntries)
                .doesNotThrowAnyException();
        // then
        assertThat(logVerifier.getAllLoggedEvents().size()).isEqualTo(0);
    }

    @Test
    void hasSingleEntryShouldWork(LogVerifier logVerifier) {
        // given
        log.info(TEST_MESSAGE);
        // when
        assertThatCode(logVerifier::hasSingleEntry)
                .doesNotThrowAnyException();
        assertThatCode(() -> logVerifier.hasSingleEntry(Level.INFO))
                .doesNotThrowAnyException();
        // then
        assertThat(logVerifier.getAllLoggedEvents().size()).isEqualTo(1);
    }

    @Test
    void shouldConfigureLogger(@Qualifier("third.party") LogVerifier logVerifier) {
        log.warn("WARNING");
        logVerifier.hasNoWarnings();
    }

    @Test
    void configuredLoggerShouldFailAssertion(@Qualifier("third.party") LogVerifier logVerifier) {
        thirdPartyLog.warn("WARNING");
        Assertions.assertThatExceptionOfType(AssertionError.class)
                .isThrownBy(logVerifier::hasNoWarnings);
    }

    private void logOnEveryLevel(final Logger logger) {
        logger.trace(TEST_MESSAGE);
        logger.debug(TEST_MESSAGE);
        logger.info(TEST_MESSAGE);
        logger.warn(TEST_MESSAGE);
        logger.error(TEST_MESSAGE);
    }
}
