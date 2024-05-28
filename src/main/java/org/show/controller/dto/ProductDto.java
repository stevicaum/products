package org.show.controller.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.Objects;

public class ProductDto extends  ProductNoIdDto{

    @NotBlank(message = "This field can't be empty")
    private Long id;

    protected ProductDto() {

    }

    public Long getId() {
        return id;
    }

    public ProductDto(final Long id, final String name, final BigDecimal price) {
        super(name, price);
        this.id = id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ProductDto.class != o.getClass()) {
            return false;
        }
        final ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id + super.toString()+
                "} ";
    }
}
