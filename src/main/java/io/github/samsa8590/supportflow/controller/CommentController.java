package io.github.samsa8590.supportflow.controller;

import io.github.samsa8590.supportflow.dto.request.CreateCommentRequest;
import io.github.samsa8590.supportflow.dto.response.CommentResponse;
import io.github.samsa8590.supportflow.exception.ErrorResponse;
import io.github.samsa8590.supportflow.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
@Tag(name = "Comments", description = "Комментарии к тикетам")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Добавить комментарий",
            description = "К закрытому тикету комментарий добавить нельзя.")
    @ApiResponse(responseCode = "409", description = "Тикет закрыт",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse add(@PathVariable Long ticketId,
                               @Valid @RequestBody CreateCommentRequest request) {
        return commentService.add(ticketId, request);
    }

    @Operation(summary = "Комментарии тикета (от старых к новым)")
    @GetMapping
    public List<CommentResponse> list(@PathVariable Long ticketId) {
        return commentService.list(ticketId);
    }
}
