package com.bucott.store.dto.product;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class ProductCreateUpdateRequestDTO {
    private String name;
    private String description;
    private Double price;
    private Double cost;
    private int currentStock;
    private Long[] categoryIds;
}
