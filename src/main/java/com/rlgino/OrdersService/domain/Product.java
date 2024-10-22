package com.rlgino.OrdersService.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "PRODUCT")
public class Product {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "NAME")
    private String name;
    @Column(name = "PRICE")
    private BigDecimal price;
    @Column(name="deletedAt")
    @JsonIgnore
    private Date deleteAt;

    public Product() {
    }

    public Product(UUID id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getDeleteAt() {
        return deleteAt;
    }

    public Product delete() {
        final Product deletedProduct = new Product(this.getId(), this.getName(), this.price);
        deletedProduct.deleteAt = new Date();
        return deletedProduct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product product)) return false;
        return id.equals(product.id) && name.equals(product.name) && price.setScale(2).equals(product.price.setScale(2)) && Objects.equals(deleteAt, product.deleteAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, deleteAt);
    }
}
