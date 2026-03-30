package com.devsuperior.dscommerce.services;


import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.dto.ProductMinDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscommerce.tests.ProductFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService service;

    @Mock
    private ProductRepository repository;

    private long existingProductId, nonexistingProductId, dependentProductId;
    private Product product;
    private String productName;
    private PageImpl<Product> page;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {

        existingProductId = 1L;
        nonexistingProductId = 2L;
        dependentProductId = 3L;

        productName = "PlayStation 5";

        product = ProductFactory.createProduct(productName);

        productDTO = new ProductDTO(product);
        page = new PageImpl<>(List.of(product));


        // findById
        Mockito.when(repository.findById(existingProductId)).thenReturn(Optional.of(product));
        Mockito.when(repository.findById(nonexistingProductId)).thenReturn(Optional.empty());
        //Page
        Mockito.when(repository.searchByName(any(), (Pageable) any())).thenReturn(page);
        //Insert
        Mockito.when(repository.save(any())).thenReturn(product);
        // update
        Mockito.when(repository.getReferenceById(existingProductId)).thenReturn(product);
        Mockito.when(repository.getReferenceById(nonexistingProductId)).thenThrow(EntityNotFoundException.class);
        Mockito.when(repository.save(product)).thenReturn(product);
        //delete
        Mockito.when(repository.existsById(existingProductId)).thenReturn(true);
        Mockito.when(repository.existsById(dependentProductId)).thenReturn(true);
        Mockito.when(repository.existsById(nonexistingProductId)).thenReturn(false);

        Mockito.doNothing().when(repository).deleteById(existingProductId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentProductId);

    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO result = service.findById(existingProductId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), product.getName());
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonexistingProductId);
        });
    }

    @Test
    public void findAllShouyldReturnPageProductMinDTO() {
        Pageable pageable = PageRequest.of(0, 12);

        Page<ProductMinDTO> result = service.findAll(productName, pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getSize());
        Assertions.assertEquals(product.getName(), result.getContent().get(0).getName());
    }

    @Test
    public void insertShouldReturnProduct() {
        ProductDTO result = service.insert(productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), product.getId());
    }
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

       ProductDTO result = service.update(existingProductId, productDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingProductId);
        Assertions.assertEquals(result.getName(), productDTO.getName());
    }
    @Test
    public void updateShouldReturnResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonexistingProductId, productDTO);
        });
    }
    @Test
    public void deleteShouldDoNothingWhenIdExist(){
        Assertions.assertDoesNotThrow(() -> {
            service.delete(existingProductId);
        });
    }
    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonexistingProductId);


        });
    }
    @Test
    public void deleteShouldThrowDatabaseExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            service.delete(dependentProductId);

        });
}

}

