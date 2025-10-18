package io.github.milczekt1.chassis.test.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Collections.unmodifiableList;
import static org.assertj.core.api.Assertions.assertThat;

public class LogVerifier {
    private final ListAppender<ILoggingEvent> logAppender;

    public LogVerifier(String loggerName) {
        Logger logger = (Logger) LoggerFactory.getLogger(loggerName);
        this.logAppender = new ListAppender<>();
        this.logAppender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        logger.setLevel(Level.TRACE);
        logger.addAppender(logAppender);
    }

    public void start() {
        this.logAppender.start();
    }

    public void reset() {
        this.logAppender.stop();
        this.logAppender.list.clear();
    }

    public LogVerifier hasSize(int expected, String logger) {
        assertThat(getAllLoggedEvents().stream()
                .filter(event -> event.getLoggerName().contains(logger))
                .count()).isEqualTo(expected);
        return this;
    }

    public LogVerifier hasSize(int expected) {
        assertThat(getAllLoggedEvents()).hasSize(expected);
        return this;
    }

    public LogVerifier hasSize(int expected, Level level) {
        assertThat(getEntriesWithLevel(level)).hasSize(expected);
        return this;
    }

    public LogVerifier hasNoEntries() {
        assertThat(getAllLoggedEvents()).isEmpty();
        return this;
    }

    public LogVerifier hasSingleEntry() {
        assertThat(getAllLoggedEvents()).hasSize(1);
        return this;
    }

    public LogVerifier hasSingleEntry(Level level) {
        assertThat(getEntriesWithLevel(level)).hasSize(1);
        return this;
    }


    public LogVerifier hasNoInfo() {
        assertThat(getEntriesWithLevel(Level.INFO)).isEmpty();
        return this;
    }

    public LogVerifier hasNoTrace() {
        assertThat(getEntriesWithLevel(Level.TRACE)).isEmpty();
        return this;
    }

    public LogVerifier hasNoDebug() {
        assertThat(getEntriesWithLevel(Level.DEBUG)).isEmpty();
        return this;
    }

    public LogVerifier hasNoWarnings() {
        assertThat(getEntriesWithLevel(Level.WARN)).isEmpty();
        return this;
    }

    public LogVerifier hasNoErrors() {
        assertThat(getEntriesWithLevel(Level.ERROR)).isEmpty();
        return this;
    }

    public LogVerifier containsTrace(String text) {
        return contains(text, Level.TRACE);
    }

    public LogVerifier containsDebug(String text) {
        return contains(text, Level.DEBUG);
    }

    public LogVerifier containsInfo(String text) {
        return contains(text, Level.INFO);
    }

    public LogVerifier containsWarn(String text) {
        return contains(text, Level.WARN);
    }

    public LogVerifier containsError(String text) {
        return contains(text, Level.ERROR);
    }

    public LogVerifier contains(String text, Level level) {
        assertThat(getAllLoggedEvents().stream()
                .anyMatch(event -> event.toString().contains(text) && event.getLevel().equals(level)))
                .isTrue();
        return this;
    }

    public List<ILoggingEvent> getAllLoggedEvents() {
        return unmodifiableList(this.logAppender.list);
    }

    private List<ILoggingEvent> getEntriesWithLevel(Level level) {
        return getAllLoggedEvents().stream()
                .filter(iLoggingEvent -> iLoggingEvent.getLevel() == level)
                .toList();
    }
}
