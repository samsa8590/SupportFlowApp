package io.github.samsa8590.supportflow.dto.request;

import io.github.samsa8590.supportflow.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull TicketStatus status
) {
}
