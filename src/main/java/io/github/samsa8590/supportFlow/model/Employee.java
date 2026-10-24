package com.supportflow.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Locale;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    protected Employee() {
    }

    public Employee(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Имя сотрудника не может быть пустым");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email сотрудника не может быть пустым");
        }
        this.name = name.trim();
        this.email = email.trim().toLowerCase(Locale.ROOT);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Employee other && id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Employee.class.hashCode();
    }

    @Override
    public String toString() {
        return name + " (id=" + id + ")";
    }
}