package io.github.samsa8590.supportflow.repository;

import io.github.samsa8590.supportflow.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);
}