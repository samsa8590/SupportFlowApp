package io.github.samsa8590.supportflow.controller;

import io.github.samsa8590.supportflow.dto.request.CreateCommentRequest;
import io.github.samsa8590.supportflow.dto.response.CommentResponse;
import io.github.samsa8590.supportflow.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse add(@PathVariable Long ticketId,
                               @Valid @RequestBody CreateCommentRequest request) {
        return commentService.add(ticketId, request);
    }

    @GetMapping
    public List<CommentResponse> list(@PathVariable Long ticketId) {
        return commentService.list(ticketId);
    }
}
