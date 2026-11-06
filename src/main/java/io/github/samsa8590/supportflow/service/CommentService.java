package io.github.samsa8590.supportflow.service;

import io.github.samsa8590.supportflow.dto.request.CreateCommentRequest;
import io.github.samsa8590.supportflow.dto.response.CommentResponse;
import io.github.samsa8590.supportflow.entity.Comment;
import io.github.samsa8590.supportflow.entity.Employee;
import io.github.samsa8590.supportflow.entity.Ticket;
import io.github.samsa8590.supportflow.enums.TicketStatus;
import io.github.samsa8590.supportflow.exception.BusinessException;
import io.github.samsa8590.supportflow.exception.ResourceNotFoundException;
import io.github.samsa8590.supportflow.mapper.CommentMapper;
import io.github.samsa8590.supportflow.repository.CommentRepository;
import io.github.samsa8590.supportflow.repository.EmployeeRepository;
import io.github.samsa8590.supportflow.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final EmployeeRepository employeeRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponse add(Long ticketId, CreateCommentRequest request) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", ticketId));
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessException("Cannot comment on a closed ticket");
        }

        Employee author = employeeRepository.findById(request.authorId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", request.authorId()));

        Comment comment = new Comment();
        comment.setTicket(ticket);
        comment.setAuthor(author);
        comment.setText(request.text());

        return commentMapper.toResponse(commentRepository.save(comment));
    }

    public List<CommentResponse> list(Long ticketId) {
        if (!ticketRepository.existsById(ticketId)) {
            throw new ResourceNotFoundException("Ticket", ticketId);
        }
        return commentRepository.findByTicketIdOrderByCreatedAtAsc(ticketId).stream()
                .map(commentMapper::toResponse)
                .toList();
    }
}
