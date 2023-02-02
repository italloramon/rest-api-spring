package com.ramon.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ramon.model.Employee;
import com.ramon.model.Order;
import com.ramon.model.Status;
import com.ramon.repository.EmployeeRepository;
import com.ramon.repository.OrderRepository;

@Configuration
class LoadDatabase {

  private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

  @Bean
  CommandLineRunner initDatabase(EmployeeRepository employeeRepository, OrderRepository orderRepository) {

    return args -> {
      employeeRepository.save(new Employee("Peter", "Parker", "Frontend"));
      employeeRepository.save(new Employee("Tony", "Stark", "Backend"));
      
      employeeRepository.findAll().forEach(employee -> log.info("Preloaded " + employee));
      
      orderRepository.save(new Order("A job", Status.COMPLETED));
      orderRepository.save(new Order("A laptop", Status.IN_PROGRESS));

      orderRepository.findAll().forEach(order -> log.info("Preloaded " + order));

    };
  }
}