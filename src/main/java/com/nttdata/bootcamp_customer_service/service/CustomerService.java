package com.nttdata.bootcamp_customer_service.service;

import com.nttdata.bootcamp_customer_service.model.collection.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerService {
    Flux<Customer> getAllCustomers();

    Flux<Customer> getCustomersByIdIn(List<String> customerIds);

    Mono<Customer> getCustomerById(String id);

    Mono<Customer> createCustomer(Customer customer);

    Mono<Customer> updateCustomer(String id, Customer customer);

    Mono<Void> deleteCustomer(String id);
}
