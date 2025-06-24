package com.bucott.store.model.product;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data @Entity @NoArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal cost;
    private int currentStock;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<ProductCategory> categories = new HashSet<>();

    public Product(String name, String description, BigDecimal price, BigDecimal cost, int currentStock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.cost = cost;
        this.currentStock = currentStock;
    }
}
