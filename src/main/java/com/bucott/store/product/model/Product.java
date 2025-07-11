package com.bucott.store.product.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotNull @Size(min=5, max=50)
    private String name;

    @NotNull @Size(min=10, max=500)
    private String description;

    @Positive
    private BigDecimal price;

    @Positive
    private BigDecimal cost;

    @NotNull
    private int currentStock;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @NotNull
    @ManyToMany
    @JoinTable(
        name = "product_category_mapping",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<ProductCategory> categories = new HashSet<>();

    public Product(String name, String description, BigDecimal price, BigDecimal cost, int currentStock) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.cost = cost;
        this.currentStock = currentStock;
    }
}
