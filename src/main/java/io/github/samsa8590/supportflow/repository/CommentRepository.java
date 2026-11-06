package io.github.samsa8590.supportflow.repository;

import io.github.samsa8590.supportflow.entity.Comment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<Comment> findByTicketIdOrderByCreatedAtAsc(Long ticketId);
}
