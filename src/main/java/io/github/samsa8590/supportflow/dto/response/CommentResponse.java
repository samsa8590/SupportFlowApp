package io.github.samsa8590.supportflow.dto.response;

import java.time.Instant;

public record CommentResponse(
        Long id,
        Long ticketId,
        EmployeeResponse author,
        String text,
        Instant createdAt
) {
}
