package com.bucott.store.dto.product;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class ProductInfoDTO {
    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal cost;
    private int currentStock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> categoryNames; // Category names instead of full objects for cleaner response
}
