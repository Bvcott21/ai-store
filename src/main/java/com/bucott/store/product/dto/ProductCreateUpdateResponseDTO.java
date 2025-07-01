package com.bucott.store.product.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProductCreateUpdateResponseDTO {
    private Long productId;
    private String name;
    private String description;
    private Double price;
    private Double cost;
    private int currentStock;
    private Long[] categoryIds;
    private String createdAt;
    private String updatedAt;
}
