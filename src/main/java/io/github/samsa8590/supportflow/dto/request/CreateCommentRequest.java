package io.github.samsa8590.supportflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
        @NotNull Long authorId,
        @NotBlank @Size(max = 5000) String text
) {
}
