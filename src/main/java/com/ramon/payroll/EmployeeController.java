package com.ramon.payroll;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/api/employees")
    public List<Employee> list() {
        return this.employeeRepository.findAll();
    }

    @PostMapping("/api/employees")
    public Employee newEmployee(@RequestBody Employee employee) {
        return this.employeeRepository.save(employee);
    }

    @GetMapping("/api/employees/{id}")
    public Employee findEmployee(@PathVariable Long id) {
        return employeeRepository.findById(id)
        .orElseThrow(() -> new EmployeeNotFoundException(id));
    }
    
    @PutMapping("/api/employees/{id}")
    public Employee updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return this.employeeRepository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return this.employeeRepository.save(employee);
        }).orElseGet(() -> {
            newEmployee.setId(id);
            return this.employeeRepository.save(newEmployee);
        });
    }

    @DeleteMapping("/api/employees/{id}")
    public void deleteEmployee(@PathVariable Long id) {
        this.employeeRepository.deleteById(id);
    }
}
