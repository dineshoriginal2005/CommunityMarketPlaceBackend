package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.model.Product;
import com.CommunityMarketPlace.New.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")  // PUBLIC / USER product browsing
public class UserProductController {

    @Autowired
    private ProductService productService;

    /**
     * 🔹 Get ALL ACTIVE products
     * Used for homepage product list.
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveProducts() {
        List<Product> products = productService.getActiveProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * 🔹 Get Single Product by ID
     * Required for Product Details page & Order system.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * 🔹 Search products by name
     * GET /products/search?keyword=phone
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        List<Product> result = productService.searchProducts(keyword);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/active/page")
    public ResponseEntity<?> getPagedProducts(
            @RequestParam int page,
            @RequestParam int size) {

        return ResponseEntity.ok(productService.getActiveProductsPaged(page, size));
    }

    /**
     * 🔹 Filter products by price range
     * GET /products/filter?min=1000&max=5000
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterProducts(
            @RequestParam Double min,
            @RequestParam Double max
    ) {
        List<Product> filtered = productService.filterByPrice(min, max);
        return ResponseEntity.ok(filtered);
    }

    @GetMapping("/active/sort")
    public ResponseEntity<?> getSortedActiveProducts(@RequestParam String sort) {
        return ResponseEntity.ok(productService.getActiveProductsSorted(sort));
    }

    @GetMapping("/category")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductsByCategory(name));
    }

    @GetMapping("/brand")
    public ResponseEntity<?> getProductsByBrand(@RequestParam String name) {
        return ResponseEntity.ok(productService.getProductsByBrand(name));
    }

    /**
     * 🔹 FIXED ADVANCED FILTER
     * Changes:
     * 1. Added 'keyword' to support text search inside filters.
     * 2. Changed 'double' to 'Double' to handle nulls (prevents 400 Bad Request).
     * 3. Sets default 0.0 and MAX_VALUE if price is empty.
     */
    @GetMapping("/filter-advanced")
    public ResponseEntity<?> advancedFilter(
            @RequestParam(required = false) String keyword,  // New Parameter
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double min,      // Changed from double to Double
            @RequestParam(required = false) Double max       // Changed from double to Double
    ) {
        // Set safe defaults if values are missing (null)
        double minPrice = (min != null) ? min : 0.0;
        double maxPrice = (max != null) ? max : Double.MAX_VALUE;

        // Pass all 5 arguments to service
        return ResponseEntity.ok(productService.advancedFilter(keyword, category, brand, minPrice, maxPrice));
    }
}