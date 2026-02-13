package com.CommunityMarketPlace.New.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminOrderDto {
    private Long orderId;

    private Long userId;
    private String buyerName;
    private String buyerEmail;

    private String orderStatus;
    private String paymentStatus;
    private String paymentMethod;

    private Double totalAmount;
    private Integer totalItems;

    private Long createdAt;
    private Long updatedAt;

    private List<AdminOrderItemDto> items;
}
