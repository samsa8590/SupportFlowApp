package io.github.samsa8590.supportflow.mapper;

import io.github.samsa8590.supportflow.dto.response.TicketResponse;
import io.github.samsa8590.supportflow.entity.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketMapper {

    private final EmployeeMapper employeeMapper;

    public TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                employeeMapper.toResponse(t.getAuthor()),
                employeeMapper.toResponse(t.getAssignee()),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
