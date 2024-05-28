package org.show.controller.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductNoIdDto {
    @NotBlank(message = "This field can't be empty")
    private String name;
    @DecimalMin(value = "0.0", inclusive = false, message = "Price Must be positive with two decimals")
    @Digits(integer=6, fraction=2)
    private BigDecimal price;
    protected ProductNoIdDto() {

    }

    public ProductNoIdDto(final String name, final BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", price=" + price ;
    }
}
