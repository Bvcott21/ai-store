package com.bucott.store.exception.product;

/**
 * Exception thrown when a requested product is not found
 */
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
    
    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public ProductNotFoundException(Long productId) {
        super(String.format("Product with ID %d not found", productId));
    }
}
