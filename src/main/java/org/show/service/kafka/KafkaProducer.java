package org.show.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.show.controller.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private static final String HEADER_SOURCE = "source";
    private static final String HEADER_SOURCE_VALUE = "products-app";
    private static final String HEADER_OPERATION = "operation";
    private static final String HEADER_TYPE = "type";
    private static final String HEADER_TYPE_PRODUCT = "product";
    private final KafkaTemplate<Long, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private String eventsTopic;

    public KafkaProducer(final KafkaTemplate<Long, String> kafkaTemplate, final String eventsTopic, final ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.eventsTopic = eventsTopic;
        this.objectMapper = objectMapper;
    }

    public void sendCreate(ProductDto productDto) {
        send(productDto, createHeaders(Operation.CREATE.name()));
    }

    public void sendDelete(ProductDto productDto) {
        send(productDto, createHeaders(Operation.DELETE.name()));
    }

    public void sendUpdated(ProductDto productDto) {
        send(productDto, createHeaders(Operation.UPDATE.name()));
    }

    private List<Header> createHeaders(String operation) {
        List<Header> kafkaHeaders = new ArrayList<>();
        kafkaHeaders.add(new RecordHeader(HEADER_SOURCE, HEADER_SOURCE_VALUE.getBytes(StandardCharsets.UTF_8)));
        kafkaHeaders.add(new RecordHeader(HEADER_OPERATION, operation.getBytes(StandardCharsets.UTF_8)));
        kafkaHeaders.add(new RecordHeader(HEADER_TYPE, HEADER_TYPE_PRODUCT.getBytes(StandardCharsets.UTF_8)));

        return kafkaHeaders;
    }

    private void send(ProductDto productDto, List<Header> headers) {
        try {
            String serialized = objectMapper.writeValueAsString(productDto);
            ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(eventsTopic, null, productDto.getId(), serialized, headers);
            CompletableFuture<SendResult<Long, String>> future = kafkaTemplate.send(record);
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Caught exception while trying to send item to Kafka product:{}", productDto, ex);
                } else {
                    log.info("Successfully sent to kafka product:{}", productDto);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Caught exception while trying to serialize", e);
        }
    }

}
