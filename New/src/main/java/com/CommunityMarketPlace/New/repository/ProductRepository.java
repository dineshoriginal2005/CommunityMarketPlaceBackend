package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findBySellerId(Long sellerId);
    List<Product> findByPriceBetween(Double min, Double max);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    Optional<Product> findByIdAndSellerId(Long id, Long sellerId); // REQUIRED FOR SECURITY
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByCategoryIgnoreCaseAndStatus(String category, String status);
    List<Product> findByBrandIgnoreCaseAndStatus(String brand, String status);

    // 🔥 FIXED ADVANCED FILTER (Keyword + Category + Brand + Price)
    // Updated to include 'keyword' search and ensure 'ACTIVE' status
    @Query("SELECT p FROM Product p WHERE " +
            "( :keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) ) " +
            "AND ( :category IS NULL OR LOWER(p.category) = LOWER(:category) ) " +
            "AND ( :brand IS NULL OR LOWER(p.brand) = LOWER(:brand) ) " +
            "AND ( p.price >= :min AND p.price <= :max ) " +
            "AND p.status = 'ACTIVE'")
    List<Product> advancedSearch(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("brand") String brand,
            @Param("min") double min,
            @Param("max") double max
    );
}