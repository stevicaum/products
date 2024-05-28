package org.show.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.model.Product;
import org.show.repository.ProductRepository;
import org.show.service.kafka.KafkaProducer;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private KafkaProducer kafkaProducer;


    private ObjectMapper objectMapper;

    private ProductService productService;

    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        productService = new ProductService(productRepository, kafkaProducer);
    }

    @Test
    void update(){
        Long id = 2l;
        ProductNoIdDto product = new ProductNoIdDto("test-product-name", new BigDecimal(3.2));
        Product inDB = new Product(id,"exist_in_db", new BigDecimal(1.2));
        when(productRepository.findById(id)).thenReturn(Optional.of(inDB));
        when(productRepository.save(any())).thenReturn(new Product(id,product.getName(), product.getPrice()));
        ProductDto actual = productService.update(id, product);
        assertEquals(inDB.getId(), actual.getId());
        assertEquals(product.getPrice(), actual.getPrice());
        assertEquals(product.getName(), actual.getName());
    }
}
