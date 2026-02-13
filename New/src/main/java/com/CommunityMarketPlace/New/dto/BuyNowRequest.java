package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class BuyNowRequest {
    private Long productId;
    private Integer quantity;
    private String paymentMethod;  // COD, ONLINE, etc.
}
