package org.show.service;

import org.show.controller.dto.PageResult;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.exception.ResourceNotFoundException;
import org.show.model.Product;
import org.show.repository.ProductRepository;
import org.show.service.kafka.KafkaProducer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public class ProductService {

    private final ProductRepository productRepository;
    private final KafkaProducer kafkaProducer;

    public ProductService(final ProductRepository productRepository, final KafkaProducer kafkaProducer) {
        this.productRepository = productRepository;
        this.kafkaProducer = kafkaProducer;
    }

    public ProductDto save(final ProductNoIdDto productDto) {
        Product product = new Product(productDto.getName(), productDto.getPrice());
        product = productRepository.save(product);
        ProductDto productDtoResponse = mapProduct(product);
        kafkaProducer.sendCreate(productDtoResponse);
        return productDtoResponse;
    }

    public ProductDto update(final Long id, final ProductNoIdDto productDto) {
        final Optional<Product> response = productRepository.findById(id);
        if (!response.isPresent()) {
            throw new ResourceNotFoundException(String.format(ResourceNotFoundException.PRODUCT, id));
        }
        Product product = new Product(response.get().getId(), productDto.getName(), productDto.getPrice());
        product = productRepository.save(product);
        ProductDto productDtoResponse = mapProduct(product);
        kafkaProducer.sendUpdated(productDtoResponse);
        return productDtoResponse;
    }

    public void delete(final Long id) {
        final Optional<Product> response = productRepository.findById(id);
        if (!response.isPresent()) {
            throw new ResourceNotFoundException(String.format(ResourceNotFoundException.PRODUCT, id));
        }
        productRepository.delete(response.get());
        kafkaProducer.sendDelete(mapProduct(response.get()));
    }

    public PageResult<ProductDto> findByName(final String name, final int page, final int size) {
        final Page<Product> response = productRepository.findProductsByNameContainingIgnoreCase(name, PageRequest.of(page, size));
        return new PageResult<>(response.getTotalPages(),
                response.stream().map(o -> mapProduct(o)).toList());
    }

    public ProductDto getById(final Long id) {
        final Optional<Product> response = productRepository.findById(id);
        if (!response.isPresent()) {
            throw new ResourceNotFoundException(String.format(ResourceNotFoundException.PRODUCT, id));
        }
        return mapProduct(response.get());
    }

    private ProductDto mapProduct(final Product product) {
        return new ProductDto(product.getId(), product.getName(), product.getPrice());
    }
}
