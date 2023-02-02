package com.ramon.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ramon.assembler.OrderModelAssembler;
import com.ramon.exception.OrderNotFoundException;
import com.ramon.model.Order;
import com.ramon.model.Status;
import com.ramon.repository.OrderRepository;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class OrderController {

    private final OrderRepository orderRepository;

    private final OrderModelAssembler assembler;

    public OrderController(OrderRepository orderRepository, OrderModelAssembler assembler) {
        this.orderRepository = orderRepository;    
        this.assembler = assembler;
    }

    @GetMapping("/api/orders")
    public CollectionModel<EntityModel<Order>> list() {
        List<EntityModel<Order>> orders = this.orderRepository.findAll().stream()
        .map(assembler::toModel).collect(Collectors.toList());
  
        return CollectionModel.of(orders, 
                linkTo(methodOn(EmployeeController.class).list()).withSelfRel());
    }

    @GetMapping("/api/orders/{id}")
    public EntityModel<Order> findOrder(@PathVariable Long id) {
        Order order = this.orderRepository.findById(id)
                            .orElseThrow(() -> new OrderNotFoundException(id));
        
        return this.assembler.toModel(order);

    }

    @PostMapping("/api/orders")
    public ResponseEntity<?> newOrder(@RequestBody Order order) {
        EntityModel<Order> entityModel = this.assembler.toModel(this.orderRepository.save(order));
        
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF)
                            .toUri()).body(entityModel);
    }

    @DeleteMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long id) {

    Order order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
        order.setStatus(Status.CANCELLED);
        return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create().withTitle("Method not allowed")
            .withDetail("You can't cancel an order that is in the " + order.getStatus() + " status"));
    }

    @PutMapping("/orders/{id}/complete")
    public ResponseEntity<?> complete(@PathVariable Long id) {

    Order order = orderRepository.findById(id) //
        .orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
        order.setStatus(Status.COMPLETED);
        return ResponseEntity.ok(assembler.toModel(orderRepository.save(order)));
    }

    return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
            .body(Problem.create().withTitle("Method not allowed")
            .withDetail("You can't complete an order that is in the " + order.getStatus() + " status"));
    }


}
