package com.supportflow.model;

import com.supportflow.exception.InvalidStatusTransitionException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Priority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status = TicketStatus.NEW;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Employee author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Employee assignee;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC, id ASC")
    private List<Comment> comments = new ArrayList<>();

    @Version
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Ticket() {
    }

    public Ticket(String title, String description, Priority priority, Employee author) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Тема обращения не может быть пустой");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Приоритет обязателен");
        }
        if (author == null) {
            throw new IllegalArgumentException("У обращения должен быть автор");
        }
        this.title = title.trim();
        this.description = description == null ? "" : description.trim();
        this.priority = priority;
        this.author = author;
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }

    public void changeStatus(TicketStatus target) {
        if (!status.canTransitionTo(target)) {
            throw new InvalidStatusTransitionException(status, target);
        }
        this.status = target;
        touch();
    }

    public void assignTo(Employee employee) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Нельзя назначить исполнителя на закрытое обращение");
        }
        this.assignee = employee;
        touch();
    }

    public void changePriority(Priority newPriority) {
        if (newPriority == null) {
            throw new IllegalArgumentException("Приоритет обязателен");
        }
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Нельзя менять приоритет закрытого обращения");
        }
        this.priority = newPriority;
        touch();
    }

    public Comment addComment(Employee author, String text) {
        if (status == TicketStatus.CLOSED) {
            throw new IllegalStateException("Нельзя комментировать закрытое обращение");
        }
        Comment comment = new Comment(this, author, text);
        comments.add(comment);
        touch();
        return comment;
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Priority getPriority() {
        return priority;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public Employee getAuthor() {
        return author;
    }

    public Optional<Employee> getAssignee() {
        return Optional.ofNullable(assignee);
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }
}