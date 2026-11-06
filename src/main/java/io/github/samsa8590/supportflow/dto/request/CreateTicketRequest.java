package io.github.samsa8590.supportflow.dto.request;

import io.github.samsa8590.supportflow.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTicketRequest(
        @NotBlank @Size(max = 200) String title,
        @NotBlank @Size(max = 5000) String description,
        Priority priority,
        @NotNull Long authorId
) {
}
