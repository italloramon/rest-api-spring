package com.ramon.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ramon.controller.EmployeeController;
import com.ramon.model.Employee;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {

    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        return EntityModel.of(employee,
            linkTo(methodOn(EmployeeController.class).findEmployee(employee.getId())).withSelfRel(),
            linkTo(methodOn(EmployeeController.class).list()).withRel("employees"));
    }
    
}
