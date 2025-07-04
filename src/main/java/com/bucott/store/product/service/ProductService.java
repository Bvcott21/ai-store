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
    ProductInfoDTO getProductById(Long productId);
    ProductCreateUpdateResponseDTO createProduct(ProductCreateUpdateRequestDTO product);
    ProductCreateUpdateResponseDTO updateProduct(Long productId, ProductCreateUpdateRequestDTO product);
    void deleteProduct(Long productId);
    List<ProductInfoDTO> getProductsByCategory(Long categoryId);
    List<ProductInfoDTO> searchProducts(String keyword);
    List<ProductInfoDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<ProductInfoDTO> getProductsByStockAvailability(boolean inStock);
    List<ProductInfoDTO> getProductsByName(String name);
    List<ProductInfoDTO> getProductsByDescription(String description);
    List<ProductInfoDTO> getProductsByCategoryAndPriceRange(Long categoryId, Double minPrice, Double maxPrice);

}
