package io.github.samsa8590.supportflow.repository;

import io.github.samsa8590.supportflow.entity.Ticket;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.enums.Priority;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    List<Ticket> findByStatus(TicketStatus status);

    List<Ticket> findByPriority(Priority priority);

    List<Ticket> findByEmployeeId(Long employeeId);
}