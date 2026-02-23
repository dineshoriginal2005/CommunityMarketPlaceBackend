package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class PlaceOrderRequest {

    private String paymentMethod;

    // 🔥 Shipping Details
    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;
    private String shippingCountry;
}
