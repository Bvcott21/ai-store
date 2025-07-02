package com.bucott.store.product.dto;

import lombok.Builder;
import lombok.Data;

public record ProductCreateUpdateResponseDTO (
    Long productId,
    String name,
    String description,
    Double price,
    Double cost,
    int currentStock,
    Long[] categoryIds,
    String createdAt,
    String updatedAt
) { }
