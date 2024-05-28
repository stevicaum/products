package org.show.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import org.show.controller.dto.PageResult;
import org.show.controller.dto.ProductDto;
import org.show.controller.dto.ProductNoIdDto;
import org.show.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ProductController.PRODUCTS)
@Tag(name = ProductController.PRODUCTS)
@Validated
public class ProductController {

    public static final String PRODUCTS = "/products";
    private final ProductService productService;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto create(@Valid @RequestBody ProductNoIdDto productDto){
        return productService.save(productDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public PageResult<ProductDto> getProductsByName(@RequestParam @NotBlank final String name,
                                                    @RequestParam(defaultValue = "0") final int page,
                                                    @RequestParam(defaultValue = "10") final int size){
        return productService.findByName(name, page, size);
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('USER')")
    public ProductDto getById(@PositiveOrZero @PathVariable final Long id){
        return productService.getById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PositiveOrZero @PathVariable final Long id){
        productService.delete(id);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto update(@PositiveOrZero @PathVariable final Long id,
                             @Valid @RequestBody ProductNoIdDto productDto){
        return productService.update(id, productDto);
    }
}
