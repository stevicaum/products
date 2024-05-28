package org.show.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.repository.ProductRepository;
import org.show.service.kafka.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

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
    private BlockingQueue<ConsumerRecord<Long, String>> consumptionQueue = new LinkedBlockingDeque<>();
    private ObjectMapper objectMapper;
    private ProductService productService;

    @BeforeEach
    void init() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JavaTimeModule());
        productService = new ProductService(productRepository, new KafkaProducer(kafkaTemplate, EVENT_TOPIC,objectMapper));
    }

    @KafkaListener(topics = EVENT_TOPIC, groupId = "listener")
    private void listen(ConsumerRecord<Long, String> consumerRecord) throws InterruptedException {
        consumptionQueue.put(consumerRecord);
    }

    @Test
    @Sql("/sql/oneProductInDb.sql")
    void saveProduct() throws JsonProcessingException, InterruptedException {
        assertEquals(1, productRepository.count());
        ProductNoIdDto productForSave = new ProductNoIdDto("test-product-name", new BigDecimal(3.2));
        Long id = 2l;
        ProductDto response = productService.save(productForSave);
        assertEquals(2, productRepository.count());
        assertEquals(id, response.getId());
        assertEquals(productForSave.getName(), response.getName());
        assertEquals(productForSave.getPrice(), response.getPrice());

        ConsumerRecord<Long, String> consumerRecord = consumptionQueue.poll();
        int retry = 0;
        while(consumerRecord==null&&retry<5){
            retry++;
            Thread.sleep(5000);
            System.out.println("Number of retry="+retry);
            consumerRecord = consumptionQueue.poll();
        }
        ProductDto actual =objectMapper.readValue(consumerRecord.value(), ProductDto.class);
        assertEquals(id,actual.getId() );
        assertEquals(productForSave.getName(),actual.getName() );
        assertEquals(productForSave.getPrice(),actual.getPrice() );
    }

}
