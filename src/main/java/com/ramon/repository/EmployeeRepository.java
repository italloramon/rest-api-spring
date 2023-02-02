package com.ramon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ramon.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
}
