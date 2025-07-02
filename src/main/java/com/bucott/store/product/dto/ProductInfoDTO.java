package com.bucott.store.product.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record ProductInfoDTO (
    Long productId,
    String name,
    String description,
    BigDecimal price,
    BigDecimal cost,
    int currentStock,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<String> categoryNames // Category names instead of full objects for cleaner response
) { }
