package com.devsuperior.dscommerce.services;

import com.devsuperior.dscommerce.dto.OrderDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest {

    @Autowired
    private OrderService service;

    @Test
    void shouldReturnOrderById() {
        Long existingId = 1L;

        OrderDTO result = service.findById(existingId);

        Assertions.assertNotNull(result);
    }
}
