package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class UserLastOrderDto {
    private Long orderId;
    private String orderStatus;
    private String paymentStatus;
    private double totalAmount;
    private long createdAt;
}
