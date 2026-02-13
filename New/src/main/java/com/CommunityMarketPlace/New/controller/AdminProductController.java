package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.model.Product;
import com.CommunityMarketPlace.New.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/products")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProductsForAdmin());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getAdminProductById(id));
    }

    @PutMapping("/{id}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> blockProduct(@PathVariable Long id) {
        productService.updateProductStatus(id, "BLOCKED");
        return ResponseEntity.ok("Product blocked successfully");
    }

    @PutMapping("/{id}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unblockProduct(@PathVariable Long id) {
        productService.updateProductStatus(id, "ACTIVE");
        return ResponseEntity.ok("Product unblocked successfully");
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchProducts(keyword));
    }
}
