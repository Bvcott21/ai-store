package com.bucott.store.product.repository;

import com.bucott.store.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Find all products by category ID
    List<Product> findByCategories_ProductCategoryId(Long categoryId);
    // find all producgts by name contains
    List<Product> findByNameContainingIgnoreCase(String name);
    // find all products by description contains
    List<Product> findByDescriptionContainingIgnoreCase(String description);
    //find all products by price range
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    // find all products that have stock greater than 0
    List<Product> findByCurrentStockGreaterThan(int stock);
    // find all products that have stock of 0 or less
    List<Product> findByCurrentStockLessThanEqual(int stock);
    // find by name and or description contains
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);
    // find all products by category and price range
    List<Product> findByCategories_ProductCategoryIdAndPriceBetween(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
}
