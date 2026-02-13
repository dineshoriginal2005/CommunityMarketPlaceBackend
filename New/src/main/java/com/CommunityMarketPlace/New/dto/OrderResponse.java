package com.CommunityMarketPlace.New.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;

    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;

    private Double totalAmount;
    private Integer totalItems;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPincode;
    private String shippingCountry;

    private Long createdAt;
    private Long updatedAt;

    private List<OrderItemResponse> items;
    private List<PaymentDetailsResponse> payments;
}
