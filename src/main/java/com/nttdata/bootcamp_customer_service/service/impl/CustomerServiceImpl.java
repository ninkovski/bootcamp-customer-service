package com.nttdata.bootcamp_customer_service.service.impl;

import com.nttdata.bootcamp_customer_service.model.collection.Customer;
import com.nttdata.bootcamp_customer_service.repository.CustomerRepository;
import com.nttdata.bootcamp_customer_service.service.CustomerService;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ReactiveRedisTemplate<String, Customer> reactiveRedisTemplate;

    public CustomerServiceImpl(CustomerRepository customerRepository, ReactiveRedisTemplate<String, Customer> reactiveRedisTemplate) {
        this.customerRepository = customerRepository;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll()
                .doOnNext(customer -> {
                    reactiveRedisTemplate.opsForValue().set("customer:" + customer.getId(), customer).subscribe();
                });
    }

    @Override
    public Flux<Customer> getCustomersByIdIn(List<String> customerIds) {
        return Flux.fromIterable(customerIds)
                .flatMap(customerId -> reactiveRedisTemplate.opsForValue().get("customer:" + customerId)
                        .switchIfEmpty(customerRepository.findById(customerId)
                                .doOnNext(customer -> {
                                    reactiveRedisTemplate.opsForValue().set("customer:" + customer.getId(), customer).subscribe();
                                })));
    }

    @Override
    public Mono<Customer> getCustomerById(String id) {
        return reactiveRedisTemplate.opsForValue().get("customer:" + id)
                .switchIfEmpty(customerRepository.findById(id)
                        .doOnNext(customer -> {
                            reactiveRedisTemplate.opsForValue().set("customer:" + customer.getId(), customer).subscribe();
                        }));
    }

    @Override
    public Mono<Customer> createCustomer(Customer customer) {
        return customerRepository.save(customer)
                .doOnNext(savedCustomer -> {
                    // Guardar el nuevo cliente en Redis
                    reactiveRedisTemplate.opsForValue().set("customer:" + savedCustomer.getId(), savedCustomer).subscribe();
                });
    }

    @Override
    public Mono<Customer> updateCustomer(String id, Customer customer) {
        return customerRepository.findById(id)
                .flatMap(existingCustomer -> {
                    existingCustomer.setName(customer.getName());
                    existingCustomer.setDocumentNumber(customer.getDocumentNumber());
                    existingCustomer.setCustomerType(customer.getCustomerType());
                    existingCustomer.setEmail(customer.getEmail());
                    existingCustomer.setPhoneNumber(customer.getPhoneNumber());

                    return customerRepository.save(existingCustomer)
                            .doOnNext(updatedCustomer -> {
                                reactiveRedisTemplate.opsForValue().set("customer:" + updatedCustomer.getId(), updatedCustomer).subscribe();
                            });
                });
    }

    @Override
    public Mono<Void> deleteCustomer(String id) {
        return customerRepository.deleteById(id)
                .doOnTerminate(() -> {
                    reactiveRedisTemplate.opsForValue().delete("customer:" + id).subscribe();
                });
    }
}