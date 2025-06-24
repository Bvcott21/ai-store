package com.bucott.store.controller.product;

import com.bucott.store.dto.product.ProductCreateUpdateRequestDTO;
import com.bucott.store.dto.product.ProductCreateUpdateResponseDTO;
import com.bucott.store.model.product.Product;
import com.bucott.store.service.product.ProductService;
import com.bucott.store.service.product.ProductServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        log.info("Received request to fetch all products");
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }

    //genetate test
    @PostMapping
    public ResponseEntity<ProductCreateUpdateResponseDTO> createProduct(@RequestBody ProductCreateUpdateRequestDTO product) {
        log.info("Received request to create product: {}", product);
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }

    // generate tests
    @PutMapping("/{productId}")
    public ResponseEntity<ProductCreateUpdateResponseDTO> updateProduct(@PathVariable Long productId, @RequestBody ProductCreateUpdateRequestDTO product) {
        log.info("Received request to update product with ID: {}", productId);
        return ResponseEntity.ok(productService.updateProduct(productId, product));
    }

    //generate tests
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        log.info("Received request to delete product with ID: {}", productId);
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }

    // generate tests
    @GetMapping("/search/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        log.info("Received request to fetch products by category ID: {}", categoryId);
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId));
    }

    // generate tests
    @GetMapping("/search/keyword/{keyword}")
    public ResponseEntity<List<Product>> searchProductsByKeyword(@PathVariable String keyword) {
        log.info("Received request to search products by keyword: {}", keyword);
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }

    // generate tests
    @GetMapping("/search/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(@RequestParam Double minPrice, @RequestParam Double maxPrice) {
        log.info("Received request to fetch products by price range: {} - {}", minPrice, maxPrice);
        return ResponseEntity.ok(productService.getProductsByPriceRange(
            BigDecimal.valueOf(minPrice), BigDecimal.valueOf(maxPrice)));
    }

    // generate tests
    @GetMapping("/search/stock")
    public ResponseEntity<List<Product>> getProductsByStockAvailability(@RequestParam boolean inStock) {
        log.info("Received request to fetch products by stock availability: {}", inStock);
        return ResponseEntity.ok(productService.getProductsByStockAvailability(inStock));
    }

    // generate tests
    @GetMapping("/search/description/{description}")
    public ResponseEntity<List<Product>> searchProductsByDescription(@PathVariable String description) {
        log.info("Received request to search products by description: {}", description);
        return ResponseEntity.ok(productService.getProductsByDescription(description));
    }

    // generate tests
    @GetMapping("/search/category-price-range")
    public ResponseEntity<List<Product>> getProductsByCategoryAndPriceRange(
            @RequestParam Long categoryId, 
            @RequestParam Double minPrice, 
            @RequestParam Double maxPrice) {
        log.info("Received request to fetch products by category ID: {} and price range: {} - {}", categoryId, minPrice, maxPrice);
        return ResponseEntity.ok(productService.getProductsByCategoryAndPriceRange(categoryId, minPrice, maxPrice));
    }

    // generate tests
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<Product>> searchProductsByName(@PathVariable String name) {
        log.info("Received request to search products by name: {}", name);
        return ResponseEntity.ok(productService.getProductsByName(name));
    }

    
}
