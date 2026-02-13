package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class UpdateOrderItemStatusRequest {

    // CONFIRMED / PACKED / SHIPPED / DELIVERED / CANCELLED
    private String status;
}
