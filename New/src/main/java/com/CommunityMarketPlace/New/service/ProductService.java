package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.AdminProductDto;
import com.CommunityMarketPlace.New.model.Product;

import java.util.List;

public interface ProductService {

    // SELLER
    Product addProduct(Product product, Long sellerId);

    List<Product> getProductsBySeller(Long sellerId);

    Product getProductByIdForSeller(Long id, Long sellerId);

    Product updateProduct(Long id, Long sellerId, Product product);
    void updateProductStatus(Long productId, String status);

    void deleteProduct(Long id, Long sellerId);

    // USER
    Product getProductById(Long id);

    List<Product> getActiveProducts();

    List<Product> searchProducts(String keyword);
    List<Product> getActiveProductsSorted(String sort);
    List<Product> filterByPrice(Double min, Double max);
    List<Product> getActiveProductsPaged(int page, int size);
    List<Product> getProductsByBrand(String brand);

    // ADMIN
    List<Product> getAllProducts();
    List<Product> getProductsByCategory(String category);

    // 🔹 UPDATED: Added 'keyword' parameter here so the Controller can pass it
    List<Product> advancedFilter(String keyword, String category, String brand, double min, double max);

    List<AdminProductDto> getAllProductsForAdmin();
    AdminProductDto getAdminProductById(Long id);
}