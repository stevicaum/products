package org.show.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.repository.ProductRepository;
import org.show.service.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class ProductServiceIT {
    public static final String EVENT_TOPIC = "products";
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    KafkaTemplate<Long, String> kafkaTemplate;
    @Autowired
    private ConsumerFactory<Long, String> consumerFactory;
    private ProductService productService;
    Consumer<Long, String> consumer;

    @BeforeEach
    void setup() {
        consumer = consumerFactory.createConsumer("consumer", null);
        consumer.subscribe(Collections.singleton(EVENT_TOPIC));
        consumer.poll(0);
    }

    @AfterEach
    void after() {
        consumer.close();
    }

    @Test
    @Sql("/sql/oneProductInDb.sql")
    void saveProduct() throws JsonProcessingException, InterruptedException {
        ObjectMapper objectMapper = new ObjectMapper();
        productService = new ProductService(productRepository, new KafkaProducer(kafkaTemplate, EVENT_TOPIC, objectMapper));
        assertEquals(1, productRepository.count());
        ProductNoIdDto productForSave = new ProductNoIdDto("test-product-name", new BigDecimal(3.2));
        Long id = 2l;
        ProductDto response = productService.save(productForSave);
        assertEquals(2, productRepository.count());
        assertEquals(id, response.getId());
        assertEquals(productForSave.getName(), response.getName());
        assertEquals(productForSave.getPrice(), response.getPrice());

        ConsumerRecord<Long, String> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, EVENT_TOPIC);
        ProductDto actual = objectMapper.readValue(consumerRecord.value(), ProductDto.class);
        assertEquals(id, actual.getId());
        assertEquals(productForSave.getName(), actual.getName());
        assertEquals(productForSave.getPrice(), actual.getPrice());
    }

}
