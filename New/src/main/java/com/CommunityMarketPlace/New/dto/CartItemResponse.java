package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class CartItemResponse {

    private Long itemId;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}
