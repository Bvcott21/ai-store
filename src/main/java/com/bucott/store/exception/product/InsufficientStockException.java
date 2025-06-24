package com.bucott.store.exception.product;

/**
 * Exception thrown when there are insufficient product stock levels
 */
public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InsufficientStockException(Long productId, int requested, int available) {
        super(String.format("Insufficient stock for product ID %d. Requested: %d, Available: %d", 
                           productId, requested, available));
    }
}
