package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class PlaceOrderRequest {

    // For now: COD / ONLINE
    private String paymentMethod;
}
