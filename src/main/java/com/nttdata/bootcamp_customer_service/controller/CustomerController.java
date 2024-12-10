package com.nttdata.bootcamp_customer_service.controller;

import com.nttdata.bootcamp_customer_service.model.collection.Customer;
import com.nttdata.bootcamp_customer_service.service.CustomerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Flux<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public Mono<Customer> getCustomerById(@PathVariable String id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping("/in")
    public Flux<Customer> getCustomerByIdIn(@RequestBody List<String> id) {
        Flux<Customer> custormes = customerService.getCustomersByIdIn(id);
        log.info("customers {}",custormes);
        return custormes;
    }

    @PostMapping
    public Mono<Customer> createCustomer(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @PutMapping("/{id}")
    public Mono<Customer> updateCustomer(@PathVariable String id, @RequestBody Customer customer) {
        return customerService.updateCustomer(id, customer);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCustomer(@PathVariable String id) {
        return customerService.deleteCustomer(id);
    }
}
