package io.github.samsa8590.supportflow.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TicketStatusTest {

    @ParameterizedTest(name = "{0} -> {1} разрешён")
    @CsvSource({
            "OPEN, IN_PROGRESS",
            "IN_PROGRESS, OPEN",
            "IN_PROGRESS, RESOLVED",
            "RESOLVED, IN_PROGRESS",
            "RESOLVED, CLOSED"
    })
    @DisplayName("Допустимые переходы")
    void allowedTransitions(TicketStatus from, TicketStatus to) {
        assertThat(from.canTransitionTo(to)).isTrue();
    }

    @ParameterizedTest(name = "{0} -> {1} запрещён")
    @CsvSource({
            "OPEN, RESOLVED",
            "OPEN, CLOSED",
            "OPEN, OPEN",
            "IN_PROGRESS, CLOSED",
            "IN_PROGRESS, IN_PROGRESS",
            "RESOLVED, OPEN",
            "RESOLVED, RESOLVED",
            "CLOSED, OPEN",
            "CLOSED, IN_PROGRESS",
            "CLOSED, RESOLVED",
            "CLOSED, CLOSED"
    })
    @DisplayName("Недопустимые переходы")
    void forbiddenTransitions(TicketStatus from, TicketStatus to) {
        assertThat(from.canTransitionTo(to)).isFalse();
    }

    @Test
    @DisplayName("Из CLOSED выхода нет")
    void closedIsTerminal() {
        assertThat(TicketStatus.CLOSED.allowedTransitions()).isEmpty();
    }

    @Test
    @DisplayName("Для каждого статуса набор переходов не содержит его самого")
    void noSelfTransitions() {
        for (TicketStatus status : TicketStatus.values()) {
            assertThat(status.allowedTransitions()).doesNotContain(status);
        }
    }
}
