package com.devsuperior.dscommerce.services;

import ch.qos.logback.core.net.server.Client;
import com.devsuperior.dscommerce.dto.OrderDTO;
import com.devsuperior.dscommerce.entities.Order;
import com.devsuperior.dscommerce.entities.OrderItem;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.entities.User;
import com.devsuperior.dscommerce.repositories.OrderItemRepository;
import com.devsuperior.dscommerce.repositories.OrderRepository;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.OrderFactory;
import com.devsuperior.dscommerce.tests.ProductFactory;
import com.devsuperior.dscommerce.tests.UserFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService service;

    @Mock
   private OrderRepository repository;

    @Mock
    private AuthService authService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private UserService userService;

    private Long existingOrderId, nonexistingOrderId;
    private Long existingProductId, nonexistingProductId;
    private Product product;
    private Order order;
    private OrderDTO orderDTO;
    private User admin, client;




    @BeforeEach
    void setUp() throws Exception {

        existingOrderId = 1L;
        nonexistingOrderId = 2L;

        existingProductId = 1L;
        nonexistingProductId = 2L;

        admin = UserFactory.createCustomAdminUser(1L, "Jef");
        client = UserFactory.createCustomClientUser(2L,"Bob");

        order = OrderFactory.createOrder(client);
        order.setId(existingOrderId);
        orderDTO = new OrderDTO(order);

        product = ProductFactory.createProduct();

        Mockito.when(repository.findById(existingOrderId)).thenReturn(Optional.of(order));
        Mockito.when(repository.findById(nonexistingOrderId)).thenReturn(Optional.empty());

        Mockito.when(productRepository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonexistingProductId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(repository.save(any())).thenReturn(order);

        Mockito.when(orderItemRepository.saveAll(any())).thenReturn(new ArrayList<>(order.getItems()));

    }
    @Test
    public void findByIdShouldRetournOrderDTOWhenIdExistsAdminLogged() {

        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingOrderId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());

    }
    @Test
    public void findByIdShouldRetournOrderDTOWhenIdExistsAndSelfClientLogged() {

        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        OrderDTO result = service.findById(existingOrderId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());
    }
    @Test
    public void findByIdShouldThrowsForbiddenExceptionWhenIdExistisAndOtherClientLogged() {
        Mockito.doThrow(ForbiddenException.class).when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ForbiddenException.class, () -> {
            OrderDTO result = service.findById(existingOrderId);
        });
    }
    @Test
    public void  findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {
        Mockito.doNothing().when(authService).validateSelfOrAdmin(any());

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            OrderDTO result = service.findById(nonexistingOrderId);
        });

    }
    @Test
    public void insertShouldReturnOrderDTOWhenAdminLogged() {
        Mockito.when(userService.authenticated()).thenReturn(admin);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());
    }

    @Test
    public void insertShouldReturnOrderDTOWhenClientLogged() {
        Mockito.when(userService.authenticated()).thenReturn(client);

        OrderDTO result = service.insert(orderDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(existingOrderId, result.getId());

    }
    @Test
    public void insertShouldThrowUsernameNotFoundExceptionWhenUserNotLogged() {
        Mockito.doThrow(UsernameNotFoundException.class).when(userService).authenticated();

        order.setClient(new User());
        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result =service.insert(orderDTO);
        });

    }
    @Test
    public void insertShouldThrowsEntityNotFoundExceptionWhenOrderProductIdDoesNotExist(){

        Mockito.when(userService.authenticated()).thenReturn(client);

        product.setId(nonexistingProductId);
        OrderItem orderItem = new OrderItem(order,product, 2,10.0);
        order.getItems().add(orderItem);

        orderDTO = new OrderDTO(order);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            @SuppressWarnings("unused")
            OrderDTO result =service.insert(orderDTO);
        });
    }
}
