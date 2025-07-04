package com.bucott.store.product.service;

import com.bucott.store.common.dto.PagedResponse;
import com.bucott.store.product.dto.ProductCreateUpdateRequestDTO;
import com.bucott.store.product.dto.ProductCreateUpdateResponseDTO;
import com.bucott.store.product.dto.ProductInfoDTO;
import com.bucott.store.product.exception.ProductNotFoundException;
import com.bucott.store.product.mapper.ProductMapper;
import com.bucott.store.product.model.Product;
import com.bucott.store.product.model.ProductCategory;
import com.bucott.store.product.repository.ProductCategoryRepository;
import com.bucott.store.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    public PagedResponse<ProductInfoDTO> getAllProducts(int page, int size, String sortBy, String sortDir) {
        log.info("Fetching paginated products: page={}, size={}, sortBy={}, sortDir={}", page, size, sortBy, sortDir);
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : 
                Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> productPage = productRepository.findAll(pageable);
        
        List<ProductInfoDTO> productDTOs = productPage.getContent()
                .stream()
                .map(productMapper::toInfoDTO)
                .collect(Collectors.toList());
        
        return PagedResponse.of(
                productDTOs,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }

    @Override
    public ProductInfoDTO getProductById(Long productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        log.info("Fetching product by ID: {}", productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        log.info("Product found: {}", product);
        return productMapper.toInfoDTO(product);
    }

    @Override
    public ProductCreateUpdateResponseDTO createProduct(ProductCreateUpdateRequestDTO productDTO) {
        log.info("Creating new product: {}", productDTO);

        if (productDTO.price() == null || productDTO.price().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid product price: {}", productDTO.price());
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (productDTO.currentStock() < 0) {
            log.error("Invalid current stock: {}", productDTO.currentStock());
            throw new IllegalArgumentException("Current stock cannot be negative");
        }

        Product product = new Product(
                productDTO.name(),
                productDTO.description(),
                productDTO.price(),
                productDTO.cost(),
                productDTO.currentStock()
        );

        product.setCategories(new HashSet<>());
        for (Long categoryId : productDTO.categoryIds()) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
            product.getCategories().add(category);
        }

        Product savedProduct = productRepository.save(product);

        // Ensure all fields are set correctly
        savedProduct.setName(product.getName());
        savedProduct.setDescription(product.getDescription());
        savedProduct.setPrice(product.getPrice());
        savedProduct.setCost(product.getCost());
        savedProduct.setCurrentStock(product.getCurrentStock());

        if (savedProduct.getPrice() == null) {
            savedProduct.setPrice(BigDecimal.ZERO); // Default value to prevent NullPointerException
        }
        if (savedProduct.getCost() == null) {
            savedProduct.setCost(BigDecimal.ZERO); // Default value to prevent NullPointerException
        }
        if (savedProduct.getCreatedAt() == null) {
            savedProduct.setCreatedAt(LocalDateTime.now()); // Default value to prevent NullPointerException
        }
        if (savedProduct.getUpdatedAt() == null) {
            savedProduct.setUpdatedAt(LocalDateTime.now()); // Default value to prevent NullPointerException
        }

        log.info("Product created successfully with ID: {}", savedProduct.getProductId());

        return new ProductCreateUpdateResponseDTO(
                savedProduct.getProductId(),
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getPrice().doubleValue(),
                savedProduct.getCost().doubleValue(),
                savedProduct.getCurrentStock(),
                savedProduct.getCategories().stream()
                        .map(ProductCategory::getProductCategoryId)
                        .toArray(Long[]::new),
                savedProduct.getCreatedAt().toString(),
                savedProduct.getUpdatedAt().toString());
    }

    @Override
    public ProductCreateUpdateResponseDTO updateProduct(Long productId, ProductCreateUpdateRequestDTO productDTO) {
        log.info("Updating product with ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.error("Product with ID {} not found for update", productId);
            throw new ProductNotFoundException(productId);
        }

        if (productDTO.price() == null || productDTO.price().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid product price: {}", productDTO.price());
            throw new IllegalArgumentException("Product price must be greater than zero");
        }

        if (productDTO.currentStock() < 0) {
            log.error("Invalid current stock: {}", productDTO.currentStock());
            throw new IllegalArgumentException("Current stock cannot be negative");
        }

        Product product = new Product(
                productDTO.name(),
                productDTO.description(),
                productDTO.price(),
                productDTO.cost(),
                productDTO.currentStock()
        );

        product.setProductId(productId);
        product.setCategories(new HashSet<>());
        for (Long categoryId : productDTO.categoryIds()) {
            ProductCategory category = productCategoryRepository.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
            product.getCategories().add(category);
        }

        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully with ID: {}", updatedProduct.getProductId());

        return new ProductCreateUpdateResponseDTO(
                updatedProduct.getProductId(),
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getPrice().doubleValue(),
                updatedProduct.getCost().doubleValue(),
                updatedProduct.getCurrentStock(),
                updatedProduct.getCategories().stream()
                        .map(ProductCategory::getProductCategoryId)
                        .toArray(Long[]::new),
                updatedProduct.getCreatedAt().toString(),
                updatedProduct.getUpdatedAt().toString());
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("Deleting product with ID: {}", productId);

        if (!productRepository.existsById(productId)) {
            log.error("Product with ID {} not found for deletion", productId);
            throw new ProductNotFoundException(productId);
        }
        productRepository.deleteById(productId);
    }

    @Override
    public List<ProductInfoDTO> getProductsByCategory(Long categoryId) {
        log.info("Fetching prodcuts by category ID: {}", categoryId);
        List<Product> products = productRepository.findByCategories_ProductCategoryId(categoryId);

        if (products.isEmpty()) {
            log.warn("No products found for category ID: {}", categoryId);
            throw new ProductNotFoundException("No products found for category ID: " + categoryId);
        }
        log.info("Found {} products for category ID: {}", products.size(), categoryId);
        return convertToProductInfoDTOList(products);
    }

    @Override
    public List<ProductInfoDTO> searchProducts(String keyword) {
        log.info("Searching products with keyword: {}", keyword);
        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        if (products.isEmpty()) {
            log.warn("No products found for keyword: {}", keyword);
            throw new ProductNotFoundException("No products found for keyword: " + keyword);
        }
        log.info("Found {} products for keyword: {}", products.size(), keyword);
        return convertToProductInfoDTOList(products);
    }

    @Override
    public List<ProductInfoDTO> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching products by price range: {} - {}", minPrice, maxPrice);
        if (minPrice == null || maxPrice == null || minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0 || minPrice.compareTo(maxPrice) > 0) {
            log.error("Invalid price range: {} - {}", minPrice, maxPrice);
            throw new IllegalArgumentException("Invalid price range provided");
        }
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        if (products.isEmpty()) {
            log.warn("No products found in the price range: {} - {}", minPrice, maxPrice);
            throw new ProductNotFoundException("No products found in the price range: " + minPrice + " - " + maxPrice);
        }
        log.info("Found {} products in the price range: {} - {}", products.size(), minPrice, maxPrice);
        return convertToProductInfoDTOList(products);
    }

    @Override
    public List<ProductInfoDTO> getProductsByStockAvailability(boolean inStock) {
        if (inStock) {
            log.info("Fetching products that are in stock");
            List<Product> products = productRepository.findByCurrentStockGreaterThan(0);
            if (products.isEmpty()) {
                log.warn("No products found that are in stock");
                throw new ProductNotFoundException("No products found that are in stock");
            }
            log.info("Found {} products that are in stock", products.size());
            return convertToProductInfoDTOList(products);
        } else {
            log.info("Fetching products that are out of stock");
            List<Product> products = productRepository.findByCurrentStockLessThanEqual(0);
            if (products.isEmpty()) {
                log.warn("No products found that are out of stock");
                throw new ProductNotFoundException("No products found that are out of stock");
            }
            log.info("Found {} products that are out of stock", products.size());
            return convertToProductInfoDTOList(products);
        }
    }

    @Override
    public List<ProductInfoDTO> getProductsByDescription(String description) {
        log.info("Fetching products by description containing: {}", description);
        if (description == null || description.isEmpty()) {
            log.error("Description cannot be null or empty");
            throw new IllegalArgumentException("Description cannot be null or empty");
        }
        List<Product> products = productRepository.findByDescriptionContainingIgnoreCase(description);
        if (products.isEmpty()) {
            log.warn("No products found with description containing: {}", description);
            throw new ProductNotFoundException("No products found with description containing: " + description);
        }
        log.info("Found {} products with description containing: {}", products.size(), description);
        return convertToProductInfoDTOList(products);
    }

    @Override
    public List<ProductInfoDTO> getProductsByCategoryAndPriceRange(Long categoryId, Double minPrice, Double maxPrice) {
        log.info("Fetching products by category ID: {} and price range: {} - {}", categoryId, minPrice, maxPrice);
        if (categoryId == null || minPrice == null || maxPrice == null || minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
            log.error("Invalid parameters provided for category ID and price range");
            throw new IllegalArgumentException("Invalid parameters provided for category ID and price range");
        }
        List<Product> products = productRepository.findByCategories_ProductCategoryIdAndPriceBetween(
                categoryId, BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice));
        if (products.isEmpty()) {
            log.warn("No products found for category ID: {} and price range: {} - {}", categoryId, minPrice, maxPrice);
            throw new ProductNotFoundException("No products found for category ID: " + categoryId + " and price range: " + minPrice + " - " + maxPrice);
        }
        log.info("Found {} products for category ID: {} and price range: {} - {}", products.size(), categoryId, minPrice, maxPrice);
        return convertToProductInfoDTOList(products);
    }

    @Override
    public List<ProductInfoDTO> getProductsByName(String name) {
        log.info("Fetching products by name containing: {}", name);
        if (name == null || name.isEmpty()) {
            log.error("Name cannot be null or empty");
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
        if (products.isEmpty()) {
            log.warn("No products found with name containing: {}", name);
            throw new ProductNotFoundException("No products found with name containing: " + name);
        }
        log.info("Found {} products with name containing: {}", products.size(), name);
        return convertToProductInfoDTOList(products);
    }

    private List<ProductInfoDTO> convertToProductInfoDTOList(List<Product> products) {
        return products.stream()
                .map(productMapper::toInfoDTO)
                .collect(Collectors.toList());
    }
}

