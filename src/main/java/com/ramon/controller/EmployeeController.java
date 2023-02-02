package com.ramon.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ramon.assembler.EmployeeModelAssembler;
import com.ramon.exception.EmployeeNotFoundException;
import com.ramon.model.Employee;
import com.ramon.repository.EmployeeRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeRepository employeeRepository, EmployeeModelAssembler assembler) {
        this.employeeRepository = employeeRepository;
        this.assembler = assembler;
    }

    @GetMapping("/api/employees")
    public CollectionModel<EntityModel<Employee>> list() {
        List<EntityModel<Employee>> employees = this.employeeRepository.findAll().stream()
        .map(assembler::toModel).collect(Collectors.toList());
  
        return CollectionModel.of(employees, 
                linkTo(methodOn(EmployeeController.class).list()).withSelfRel());
    }

    @PostMapping("/api/employees")
    public ResponseEntity<?> newEmployee(@RequestBody Employee employee) {
        EntityModel<Employee> entityModel = this.assembler.toModel(this.employeeRepository.save(employee));
        
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                            .toUri()).body(entityModel);
    }

    @GetMapping("/api/employees/{id}")
    public EntityModel<Employee> findEmployee(@PathVariable Long id) {
        Employee employee = this.employeeRepository.findById(id)
                            .orElseThrow(() -> new EmployeeNotFoundException(id));
        
        return this.assembler.toModel(employee);

    }
    
    @PutMapping("/api/employees/{id}")
    public ResponseEntity<?> updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        Employee updateEmployee = this.employeeRepository.findById(id).map(employee -> {
            employee.setName(newEmployee.getName());
            employee.setRole(newEmployee.getRole());
            return this.employeeRepository.save(employee);
        }).orElseGet(() -> {
            newEmployee.setId(id);
            return this.employeeRepository.save(newEmployee);
        });

        EntityModel<Employee> entityModel = this.assembler.toModel(updateEmployee);

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                            .toUri()).body(entityModel);
    }

    @DeleteMapping("/api/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
        this.employeeRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
