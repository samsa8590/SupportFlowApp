package io.github.samsa8590.supportflow.controller;

import io.github.samsa8590.supportflow.dto.request.AssignTicketRequest;
import io.github.samsa8590.supportflow.dto.request.CreateTicketRequest;
import io.github.samsa8590.supportflow.dto.request.UpdateTicketStatusRequest;
import io.github.samsa8590.supportflow.dto.response.TicketResponse;
import io.github.samsa8590.supportflow.enums.Priority;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @GetMapping
    public PagedModel<TicketResponse> search(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assigneeId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new PagedModel<>(ticketService.search(status, priority, assigneeId, pageable));
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id) {
        return ticketService.getById(id);
    }

    @PatchMapping("/{id}/status")
    public TicketResponse changeStatus(@PathVariable Long id,
                                       @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.changeStatus(id, request);
    }

    @PatchMapping("/{id}/assignee")
    public TicketResponse assign(@PathVariable Long id,
                                 @Valid @RequestBody AssignTicketRequest request) {
        return ticketService.assign(id, request);
    }
}
