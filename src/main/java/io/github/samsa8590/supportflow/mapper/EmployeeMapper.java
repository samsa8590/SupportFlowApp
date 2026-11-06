package io.github.samsa8590.supportflow.mapper;

import io.github.samsa8590.supportflow.dto.response.EmployeeResponse;
import io.github.samsa8590.supportflow.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeResponse toResponse(Employee e) {
        if (e == null) {
            return null;
        }
        return new EmployeeResponse(e.getId(), e.getFullName(), e.getEmail(), e.getRole());
    }
}
