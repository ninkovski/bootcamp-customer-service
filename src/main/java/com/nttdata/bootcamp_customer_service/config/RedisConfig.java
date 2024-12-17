package com.nttdata.bootcamp_customer_service.config;

import com.nttdata.bootcamp_customer_service.model.collection.Customer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Customer> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {

        // Crear un serializer de Jackson para el tipo Customer
        Jackson2JsonRedisSerializer<Customer> customerJsonSerializer = new Jackson2JsonRedisSerializer<>(Customer.class);

        // Configuración del contexto de serialización (Claves como String, Valores como JSON)
        RedisSerializationContext<String, Customer> serializationContext = RedisSerializationContext
                .<String, Customer>newSerializationContext(RedisSerializer.string())
                .value(RedisSerializationContext.SerializationPair.fromSerializer(customerJsonSerializer)) // Serialización del valor como JSON
                .build();

        // Crear y devolver el ReactiveRedisTemplate
        return new ReactiveRedisTemplate<>(connectionFactory, serializationContext);
    }
}