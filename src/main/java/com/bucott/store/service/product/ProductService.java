package com.bucott.store.service.product;

import com.bucott.store.dto.product.ProductCreateUpdateRequestDTO;
import com.bucott.store.dto.product.ProductCreateUpdateResponseDTO;
import com.bucott.store.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long productId);
    ProductCreateUpdateResponseDTO createProduct(ProductCreateUpdateRequestDTO product);
    ProductCreateUpdateResponseDTO updateProduct(Long productId, ProductCreateUpdateRequestDTO product);
    void deleteProduct(Long productId);
    List<Product> getProductsByCategory(Long categoryId);
    List<Product> searchProducts(String keyword);
    List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getProductsByStockAvailability(boolean inStock);
    List<Product> getProductsByName(String name);
    List<Product> getProductsByDescription(String description);
    List<Product> getProductsByCategoryAndPriceRange(Long categoryId, Double minPrice, Double maxPrice);

}
