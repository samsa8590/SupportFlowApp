package com.supportflow.repository;

import com.supportflow.model.Ticket;
import com.supportflow.model.TicketStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {


    @Override
    @EntityGraph(attributePaths = {"author", "assignee"})
    List<Ticket> findAll();

    @EntityGraph(attributePaths = {"author", "assignee"})
    List<Ticket> findByStatus(TicketStatus status);

    @EntityGraph(attributePaths = {"author", "assignee", "comments", "comments.author"})
    @Query("select t from Ticket t where t.id = :id")
    Optional<Ticket> findDetailedById(@Param("id") Long id);
}