package io.github.samsa8590.supportflow.controller;

import io.github.samsa8590.supportflow.dto.request.AssignTicketRequest;
import io.github.samsa8590.supportflow.dto.request.CreateTicketRequest;
import io.github.samsa8590.supportflow.dto.request.UpdateTicketStatusRequest;
import io.github.samsa8590.supportflow.dto.response.TicketResponse;
import io.github.samsa8590.supportflow.enums.Priority;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.exception.ErrorResponse;
import io.github.samsa8590.supportflow.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Создание, поиск и управление заявками")
public class TicketController {

    private final TicketService ticketService;

    @Operation(summary = "Создать тикет",
            description = "Автор должен существовать и быть активным. Приоритет необязателен (по умолчанию MEDIUM).")
    @ApiResponse(responseCode = "201", description = "Тикет создан")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Автор не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.create(request);
    }

    @Operation(summary = "Список тикетов с фильтрами и пагинацией",
            description = "Все фильтры необязательны. Сортировка: ?sort=priority,desc. По умолчанию - новые сверху.")
    @GetMapping
    public PagedModel<TicketResponse> search(
            @Parameter(description = "Фильтр по статусу") @RequestParam(required = false) TicketStatus status,
            @Parameter(description = "Фильтр по приоритету") @RequestParam(required = false) Priority priority,
            @Parameter(description = "Фильтр по id исполнителя") @RequestParam(required = false) Long assigneeId,
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return new PagedModel<>(ticketService.search(status, priority, assigneeId, pageable));
    }

    @Operation(summary = "Получить тикет по id")
    @ApiResponse(responseCode = "404", description = "Тикет не найден",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id) {
        return ticketService.getById(id);
    }

    @Operation(summary = "Сменить статус",
            description = "Допустимые переходы: OPEN→IN_PROGRESS, IN_PROGRESS→OPEN/RESOLVED, "
                    + "RESOLVED→IN_PROGRESS/CLOSED. В IN_PROGRESS можно перейти только с назначенным исполнителем.")
    @ApiResponse(responseCode = "409", description = "Недопустимый переход или конфликт версий",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{id}/status")
    public TicketResponse changeStatus(@PathVariable Long id,
                                       @Valid @RequestBody UpdateTicketStatusRequest request) {
        return ticketService.changeStatus(id, request);
    }

    @Operation(summary = "Назначить исполнителя",
            description = "Исполнитель - активный сотрудник с ролью AGENT или ADMIN. Закрытому тикету назначить нельзя.")
    @ApiResponse(responseCode = "409", description = "Нарушено бизнес-правило",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PatchMapping("/{id}/assignee")
    public TicketResponse assign(@PathVariable Long id,
                                 @Valid @RequestBody AssignTicketRequest request) {
        return ticketService.assign(id, request);
    }
}
