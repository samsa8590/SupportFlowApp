package io.github.samsa8590.supportflow.mapper;

import io.github.samsa8590.supportflow.dto.response.CommentResponse;
import io.github.samsa8590.supportflow.entity.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final EmployeeMapper employeeMapper;

    public CommentResponse toResponse(Comment c) {
        return new CommentResponse(
                c.getId(),
                c.getTicket().getId(),
                employeeMapper.toResponse(c.getAuthor()),
                c.getText(),
                c.getCreatedAt()
        );
    }
}
