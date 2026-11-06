package io.github.samsa8590.supportflow.repository;

import io.github.samsa8590.supportflow.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

public interface TicketRepository extends JpaRepository<Ticket, Long>, JpaSpecificationExecutor<Ticket> {

    @Override
    @EntityGraph(attributePaths = {"author", "assignee"})
    Page<Ticket> findAll(Specification<Ticket> spec, Pageable pageable);
}
