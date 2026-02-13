package com.CommunityMarketPlace.New.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Details
    private String name;
    private String description;

    // Pricing & Stock
    private Double price;
    private Integer quantity;
    private String category;
    private String brand;


    // Seller Ownership (MUST MATCH User.id → role = SELLER)
    @Column(nullable = false)
    private Long sellerId;

    // Product Image
    private String imageUrl;

    /**
     * ACTIVE  → visible
     * INACTIVE → hidden
     * BLOCKED → admin removed
     */
    private String status = "ACTIVE";

    // Optional timestamps (recommended)
    private Long createdAt = System.currentTimeMillis();
    private Long updatedAt = System.currentTimeMillis();


}
