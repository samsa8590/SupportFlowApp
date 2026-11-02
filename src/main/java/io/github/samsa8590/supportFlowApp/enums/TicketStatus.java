package io.github.samsa8590.supportFlowApp.enums;

import java.util.EnumSet;
import java.util.Set;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    public Set<TicketStatus> allowedTransitions() {
        return switch (this) {
            case OPEN -> EnumSet.of(IN_PROGRESS);
            case IN_PROGRESS -> EnumSet.of(OPEN, RESOLVED);
            case RESOLVED -> EnumSet.of(IN_PROGRESS, CLOSED); // reopen или закрыть
            case CLOSED -> EnumSet.noneOf(TicketStatus.class);
        };
    }

    public boolean canTransitionTo(TicketStatus next) {
        return allowedTransitions().contains(next);
    }
}

