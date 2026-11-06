package io.github.samsa8590.supportflow.dto.request;

import jakarta.validation.constraints.NotNull;

public record AssignTicketRequest(
        @NotNull Long assigneeId
) {
}
