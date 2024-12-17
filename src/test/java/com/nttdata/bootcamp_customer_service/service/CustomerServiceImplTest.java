package com.nttdata.bootcamp_customer_service.service;

import com.nttdata.bootcamp_customer_service.model.collection.Customer;
import com.nttdata.bootcamp_customer_service.repository.CustomerRepository;
import com.nttdata.bootcamp_customer_service.service.impl.CustomerServiceImpl;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations; // Mock for opsForValue()

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId("1");
        customer.setName("John Doe");
        customer.setDocumentNumber("12345678");
        customer.setCustomerType("Personal");
        customer.setEmail("johndoe@example.com");
        customer.setPhoneNumber("123456789");
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.set(anyString(), any())).thenReturn(Mono.empty());

    }

    @Test
    void getAllCustomers() {
        when(customerRepository.findAll()).thenReturn(Flux.just(customer));

        StepVerifier.create(customerService.getAllCustomers())
                .expectNext(customer)
                .verifyComplete();

        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void getCustomersByIdIn() {
        List<String> customerIds = List.of("1", "2");
        when(customerRepository.findByIdIn(customerIds)).thenReturn(Flux.just(customer));

        StepVerifier.create(customerService.getCustomersByIdIn(customerIds))
                .expectNext(customer)
                .verifyComplete();

        verify(customerRepository, times(1)).findByIdIn(customerIds);
    }

    @Test
    void getCustomerById() {
        // Mock Redis template to return a mocked ReactiveValueOperations
        when(reactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);

        // Mock the behavior of opsForValue().get() to return the customer with the correct key
        when(valueOperations.get("customer:1")).thenReturn(Mono.just(customer));  // Ensure the correct key "customer:1"

        // Mock the customer repository to return the customer from the database
        when(customerRepository.findById("1")).thenReturn(Mono.just(customer));

        // Test the service method
        StepVerifier.create(customerService.getCustomerById("1"))
                .expectNext(customer)
                .verifyComplete();

        // Verify interactions with the mocks
        verify(customerRepository, times(1)).findById("1");
        verify(reactiveRedisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get("customer:1");  // Correct key
    }

    @Test
    void createCustomer() {
        when(customerRepository.save(customer)).thenReturn(Mono.just(customer));

        StepVerifier.create(customerService.createCustomer(customer))
                .expectNext(customer)
                .verifyComplete();

        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void updateCustomer() {
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId("1");
        updatedCustomer.setName("Jane Doe");
        updatedCustomer.setDocumentNumber("87654321");
        updatedCustomer.setCustomerType("Business");
        updatedCustomer.setEmail("janedoe@example.com");
        updatedCustomer.setPhoneNumber("987654321");

        when(customerRepository.findById("1")).thenReturn(Mono.just(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(Mono.just(updatedCustomer));

        StepVerifier.create(customerService.updateCustomer("1", updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();

        verify(customerRepository, times(1)).findById("1");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }


}
