package org.show.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table ( name = "product" )
public class Product {

    @SequenceGenerator ( name = "seq_product", sequenceName = "seq_product", allocationSize = 1 )
    @Id
    @GeneratedValue ( generator = "seq_product" )
    private Long id;
    private String name;
    @Column ( name = "price" )
    private BigDecimal price;

    protected Product() {}

    public Product(final String name, final BigDecimal price ) {
        this.name = name;
        this.price = price;
    }

    public Product(final Long id, final String name, final BigDecimal price ) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o )
            return true;
        if ( o == null || getClass() != o.getClass() )
            return false;

        Product product = (Product) o;

        if ( !id.equals( product.id ) )
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
