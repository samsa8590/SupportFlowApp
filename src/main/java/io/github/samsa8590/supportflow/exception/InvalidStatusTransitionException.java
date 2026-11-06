package io.github.samsa8590.supportflow.exception;

import io.github.samsa8590.supportflow.enums.TicketStatus;

public class InvalidStatusTransitionException extends BusinessException {

    public InvalidStatusTransitionException(TicketStatus from, TicketStatus to) {
        super("Transition from " + from + " to " + to + " is not allowed. Allowed: "
                + from.allowedTransitions());
    }
}
