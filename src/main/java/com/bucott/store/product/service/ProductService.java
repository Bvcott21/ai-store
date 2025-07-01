package com.bucott.store.product.service;

import com.bucott.store.common.dto.PagedResponse;
import com.bucott.store.product.dto.ProductCreateUpdateRequestDTO;
import com.bucott.store.product.dto.ProductCreateUpdateResponseDTO;
import com.bucott.store.product.dto.ProductInfoDTO;
import com.bucott.store.product.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    PagedResponse<ProductInfoDTO> getAllProducts(int page, int size, String sortBy, String sortDir);
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
