package io.github.samsa8590.supportflow.dto.response;

import io.github.samsa8590.supportflow.enums.Role;

public record EmployeeResponse(
        Long id,
        String fullName,
        String email,
        Role role
) {
}
