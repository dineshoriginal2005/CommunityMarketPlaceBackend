package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.AdminProductDto;
import com.CommunityMarketPlace.New.model.Product;
import com.CommunityMarketPlace.New.model.Seller;
import com.CommunityMarketPlace.New.model.User;

import com.CommunityMarketPlace.New.repository.ProductRepository;
import com.CommunityMarketPlace.New.repository.SellerRepository;
import com.CommunityMarketPlace.New.repository.UserRepository;

import com.CommunityMarketPlace.New.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired private ProductRepository productRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private SellerRepository sellerRepository;

    // ============================================================
    // 🔥 ADMIN PRODUCT MAPPER
    // ============================================================
    private AdminProductDto mapToAdminProduct(Product product) {
        AdminProductDto dto = new AdminProductDto();

        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setCategory(product.getCategory());
        dto.setBrand(product.getBrand());
        dto.setStatus(product.getStatus());
        dto.setImageUrl(product.getImageUrl());

        // Fetch seller user
        User sellerUser = userRepository.findById(product.getSellerId()).orElse(null);
        if (sellerUser != null) {
            dto.setSellerId(sellerUser.getId());
            dto.setSellerName(sellerUser.getName());
            dto.setSellerEmail(sellerUser.getEmail());
            dto.setSellerVerified(sellerUser.isSellerVerified());
        }

        // Fetch seller status from Seller table
        Seller sellerInfo = sellerRepository.findById(product.getSellerId()).orElse(null);
        if (sellerInfo != null) {
            dto.setSellerStatus(sellerInfo.getStatus());
        }

        return dto;
    }

    // ============================================================
    // 🔥 SELLER SECTION
    // ============================================================

    @Override
    public Product addProduct(Product product, Long sellerId) {

        // Validate seller exists
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        // Set seller
        product.setSellerId(sellerId);
        product.setCreatedAt(System.currentTimeMillis());
        product.setUpdatedAt(System.currentTimeMillis());

        // Default status
        if (product.getStatus() == null) {
            product.setStatus("ACTIVE");
        }

        return productRepository.save(product);
    }

    @Override
    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    @Override
    public Product getProductByIdForSeller(Long id, Long sellerId) {
        return productRepository.findByIdAndSellerId(id, sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found or not owned by you"
                ));
    }

    @Override
    public Product updateProduct(Long id, Long sellerId, Product updatedProduct) {
        Product existing = getProductByIdForSeller(id, sellerId);

        existing.setName(updatedProduct.getName());
        existing.setDescription(updatedProduct.getDescription());
        existing.setPrice(updatedProduct.getPrice());
        existing.setQuantity(updatedProduct.getQuantity());
        existing.setBrand(updatedProduct.getBrand());
        existing.setCategory(updatedProduct.getCategory());
        existing.setImageUrl(updatedProduct.getImageUrl());

        existing.setUpdatedAt(System.currentTimeMillis());

        return productRepository.save(existing);
    }

    @Override
    public void deleteProduct(Long id, Long sellerId) {
        Product existing = getProductByIdForSeller(id, sellerId);
        productRepository.delete(existing);
    }

    @Override
    public void updateProductStatus(Long productId, String status) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(status);
        product.setUpdatedAt(System.currentTimeMillis());
        productRepository.save(product);
    }

    // ============================================================
    // 🔥 USER PRODUCT FUNCTIONS
    // ============================================================

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"
                ));
    }

    @Override
    public List<Product> getActiveProducts() {
        return productRepository.findAll().stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .toList();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Product> filterByPrice(Double min, Double max) {
        return productRepository.findByPriceBetween(min, max);
    }

    @Override
    public List<Product> getActiveProductsPaged(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return productRepository.findAll(pageable)
                .stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .toList();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryIgnoreCaseAndStatus(category, "ACTIVE");
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrandIgnoreCaseAndStatus(brand, "ACTIVE");
    }

    // ============================================================
    // 🔥 🔹 UPDATED FILTER METHOD
    // ============================================================
    @Override
    public List<Product> advancedFilter(String keyword, String category, String brand, double min, double max) {

        // Sanitize inputs (treat empty strings as null so Repository ignores them)
        if (keyword != null && keyword.trim().isEmpty()) keyword = null;
        if (category != null && category.trim().isEmpty()) category = null;
        if (brand != null && brand.trim().isEmpty()) brand = null;

        // Call the UPDATED repository method that accepts all 5 parameters
        return productRepository.advancedSearch(keyword, category, brand, min, max);
    }

    // ============================================================
    // 🔥 ADMIN PRODUCT FUNCTIONS
    // ============================================================

    @Override
    public List<AdminProductDto> getAllProductsForAdmin() {
        return productRepository.findAll().stream()
                .map(this::mapToAdminProduct)
                .toList();
    }

    @Override
    public AdminProductDto getAdminProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return mapToAdminProduct(product);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getActiveProductsSorted(String sort) {

        Sort sortingRule;

        switch (sort) {
            case "priceAsc" -> sortingRule = Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> sortingRule = Sort.by(Sort.Direction.DESC, "price");
            case "newest" -> sortingRule = Sort.by(Sort.Direction.DESC, "createdAt");
            case "oldest" -> sortingRule = Sort.by(Sort.Direction.ASC, "createdAt");
            default -> sortingRule = Sort.by(Sort.Direction.ASC, "name");
        }

        return productRepository.findAll(sortingRule)
                .stream()
                .filter(p -> "ACTIVE".equalsIgnoreCase(p.getStatus()))
                .toList();
    }
}