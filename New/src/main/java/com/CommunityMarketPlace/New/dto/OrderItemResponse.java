package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Long orderItemId;
    private Long productId;
    private String productName;
    private String productImage;
    private Long sellerId;
    private Integer quantity;
    private Double priceAtPurchase;
    private Double totalPrice;
    private String itemStatus;
}
