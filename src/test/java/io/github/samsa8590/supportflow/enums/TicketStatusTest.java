package io.github.samsa8590.supportflow.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Чистый юнит-тест: ни Spring, ни базы данных. Проверяем только логику enum,
 * поэтому тест выполняется за миллисекунды.
 */
class TicketStatusTest {

    // @ParameterizedTest запускает один и тот же тест для каждой строки из @CsvSource.
    // Так мы описываем всю таблицу допустимых переходов компактно, а не 6 копиями кода.
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
        // JUnit сам превращает строки "OPEN" из CSV в значения enum
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
        // Страховка на будущее: если кто-то добавит новый статус и забудет правило,
        // а заодно случайно разрешит переход "в себя", тест упадёт.
        for (TicketStatus status : TicketStatus.values()) {
            assertThat(status.allowedTransitions()).doesNotContain(status);
        }
    }
}
