package io.github.samsa8590.supportflow.dto.response;

import io.github.samsa8590.supportflow.enums.Priority;
import io.github.samsa8590.supportflow.enums.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        String title,
        String description,
        TicketStatus status,
        Priority priority,
        EmployeeResponse author,
        EmployeeResponse assignee,   // null, если не назначен
        Instant createdAt,
        Instant updatedAt
) {
}
