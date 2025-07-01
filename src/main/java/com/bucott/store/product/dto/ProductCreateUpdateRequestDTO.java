package com.bucott.store.product.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data @Builder
public class ProductCreateUpdateRequestDTO {
    
    @NotBlank(message = "Product name is required")
    @Size(min = 5, max = 50, message = "Product name must be between 5 and 50 characters")
    private String name;
    
    @NotBlank(message = "Product description is required")
    @Size(min = 10, max = 500, message = "Product description must be between 10 and 500 characters")
    private String description;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Price must not exceed $999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Price must have at most 6 digits before decimal and 2 after")
    private BigDecimal price;
    
    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.01", message = "Cost must be greater than 0")
    @DecimalMax(value = "999999.99", message = "Cost must not exceed $999,999.99")
    @Digits(integer = 6, fraction = 2, message = "Cost must have at most 6 digits before decimal and 2 after")
    private BigDecimal cost;
    
    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 999999, message = "Stock cannot exceed 999,999")
    private Integer currentStock;
    
    @NotNull(message = "At least one category must be selected")
    @Size(min = 1, max = 5, message = "Product must have between 1 and 5 categories")
    private Long[] categoryIds;
}
