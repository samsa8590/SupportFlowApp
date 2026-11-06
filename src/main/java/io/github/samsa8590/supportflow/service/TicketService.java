package io.github.samsa8590.supportflow.service;

import io.github.samsa8590.supportflow.dto.request.AssignTicketRequest;
import io.github.samsa8590.supportflow.dto.request.CreateTicketRequest;
import io.github.samsa8590.supportflow.dto.request.UpdateTicketStatusRequest;
import io.github.samsa8590.supportflow.dto.response.TicketResponse;
import io.github.samsa8590.supportflow.entity.Employee;
import io.github.samsa8590.supportflow.entity.Ticket;
import io.github.samsa8590.supportflow.enums.Priority;
import io.github.samsa8590.supportflow.enums.Role;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.exception.BusinessException;
import io.github.samsa8590.supportflow.exception.InvalidStatusTransitionException;
import io.github.samsa8590.supportflow.exception.ResourceNotFoundException;
import io.github.samsa8590.supportflow.mapper.TicketMapper;
import io.github.samsa8590.supportflow.repository.EmployeeRepository;
import io.github.samsa8590.supportflow.repository.TicketRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        Employee author = findEmployee(request.authorId());
        if (!author.isActive()) {
            throw new BusinessException("Inactive employee cannot create tickets");
        }

        Ticket ticket = new Ticket();
        ticket.setTitle(request.title());
        ticket.setDescription(request.description());
        ticket.setPriority(request.priority() != null ? request.priority() : Priority.MEDIUM);
        ticket.setAuthor(author);

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public TicketResponse getById(Long id) {
        return ticketMapper.toResponse(findTicket(id));
    }

    public Page<TicketResponse> search(TicketStatus status, Priority priority,
                                       Long assigneeId, Pageable pageable) {
        Specification<Ticket> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
        return ticketRepository.findAll(spec, pageable).map(ticketMapper::toResponse);
    }

    @Transactional
    public TicketResponse changeStatus(Long id, UpdateTicketStatusRequest request) {
        Ticket ticket = findTicket(id);
        TicketStatus current = ticket.getStatus();
        TicketStatus next = request.status();

        if (!current.canTransitionTo(next)) {
            throw new InvalidStatusTransitionException(current, next);
        }
        if (next == TicketStatus.IN_PROGRESS && ticket.getAssignee() == null) {
            throw new BusinessException("Assign the ticket to an agent before moving it to IN_PROGRESS");
        }

        ticket.setStatus(next);
        return ticketMapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponse assign(Long id, AssignTicketRequest request) {
        Ticket ticket = findTicket(id);
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessException("Cannot assign a closed ticket");
        }

        Employee assignee = findEmployee(request.assigneeId());
        if (!assignee.isActive()) {
            throw new BusinessException("Cannot assign the ticket to an inactive employee");
        }
        if (assignee.getRole() == Role.EMPLOYEE) {
            throw new BusinessException("Only AGENT or ADMIN can be assigned to a ticket");
        }

        ticket.setAssignee(assignee);
        return ticketMapper.toResponse(ticket);
    }

    private Ticket findTicket(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", id));
    }

    private Employee findEmployee(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }
}
