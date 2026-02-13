package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class AdminProductDto {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String category;
    private String brand;
    private String status;
    private String imageUrl;

    // Seller Info
    private Long sellerId;
    private String sellerName;
    private String sellerEmail;
    private boolean sellerVerified;
    private String sellerStatus; // APPROVED / REJECTED / PENDING
}
