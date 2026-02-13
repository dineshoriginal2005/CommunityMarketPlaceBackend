package com.CommunityMarketPlace.New.dto;

import lombok.Data;

import java.util.List;

@Data
public class SellerOrderResponse {

    private Long orderId;

    private String buyerName;
    private String buyerEmail;


    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;


    private Double sellerTotalAmount;
    private Integer sellerTotalItems;

    private List<OrderItemResponse> items;
}
