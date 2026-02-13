package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.model.Product;
import com.CommunityMarketPlace.New.service.CloudinaryService;
import com.CommunityMarketPlace.New.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/seller/products")
@PreAuthorize("hasRole('SELLER')")
public class SellerProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ---------------------------------------------------------
    // ✔ Get logged-in sellerId from JWT principal
    // ---------------------------------------------------------
    private Long getSellerId() {
        com.CommunityMarketPlace.New.model.User seller =
                (com.CommunityMarketPlace.New.model.User)
                        SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return seller.getId();
    }

    // ---------------------------------------------------------
    // ✔ ADD PRODUCT (SELLER)
    // ---------------------------------------------------------
    @PostMapping("/add")
    public ResponseEntity<?> addProduct(@RequestBody Product product) {
        Long sellerId = getSellerId();
        Product saved = productService.addProduct(product, sellerId);
        return ResponseEntity.ok(saved);
    }

    // ---------------------------------------------------------
    // ✔ GET ALL PRODUCTS OF LOGGED-IN SELLER
    // ---------------------------------------------------------
    @GetMapping("/my")
    public ResponseEntity<?> getMyProducts() {
        Long sellerId = getSellerId();
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    // ---------------------------------------------------------
    // ✔ GET SINGLE PRODUCT (Owned by seller only)
    // ---------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getMySingleProduct(@PathVariable Long id) {
        Long sellerId = getSellerId();
        return ResponseEntity.ok(productService.getProductByIdForSeller(id, sellerId));
    }

    // ---------------------------------------------------------
    // ✔ UPDATE PRODUCT (Seller only)
    // ---------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {

        Long sellerId = getSellerId();
        Product updated = productService.updateProduct(id, sellerId, updatedProduct);

        return ResponseEntity.ok(updated);
    }

    // ---------------------------------------------------------
    // ✔ DELETE PRODUCT (Seller only)
    // ---------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Long sellerId = getSellerId();
        productService.deleteProduct(id, sellerId);

        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    // ---------------------------------------------------------
    // ✔ UPLOAD IMAGE + SAVE URL TO PRODUCT
    // ---------------------------------------------------------
    @PostMapping("/{productId}/upload-image")
    public ResponseEntity<?> uploadImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile image) {

        Long sellerId = getSellerId();

        // Check if seller owns this product
        Product product = productService.getProductByIdForSeller(productId, sellerId);

        // Upload to Cloudinary
        String imageUrl = cloudinaryService.uploadImage(image);

        // Save image URL to DB
        product.setImageUrl(imageUrl);
        product.setUpdatedAt(System.currentTimeMillis());
        productService.updateProduct(productId, sellerId, product);

        return ResponseEntity.ok(Map.of(
                "message", "Image uploaded successfully",
                "imageUrl", imageUrl
        ));
    }
}
