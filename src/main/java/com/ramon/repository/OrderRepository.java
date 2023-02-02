package com.ramon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ramon.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
