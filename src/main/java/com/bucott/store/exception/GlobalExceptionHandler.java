package com.bucott.store.exception;

import java.util.HashMap;
import java.util.Map;

import com.bucott.store.exception.product.InsufficientStockException;
import com.bucott.store.exception.product.InvalidProductDataException;
import com.bucott.store.exception.product.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.bucott.store.exception.auth.InvalidCredentialsException;
import com.bucott.store.exception.general.InvalidInputException;
import com.bucott.store.exception.user.EmailNotFoundException;
import com.bucott.store.exception.user.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({EmailNotFoundException.class, UserNotFoundException.class, ProductNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFoundException(Exception ex) {
        return buildErrorResponse(ex, "User not found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({InvalidCredentialsException.class, InvalidInputException.class, InvalidProductDataException.class, InsufficientStockException.class})
    public ResponseEntity<Object> handleInvalidInputException(Exception ex) {
        return buildErrorResponse(ex, "Invalid input", HttpStatus.BAD_REQUEST);
    }
    
    private ResponseEntity<Object> buildErrorResponse(Exception ex, String error, HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", error);
        body.put("message", ex.getMessage());
        body.put("status", status.value());
        body.put("timestamp", System.currentTimeMillis());

        return new ResponseEntity<>(body, status);
    }
}
