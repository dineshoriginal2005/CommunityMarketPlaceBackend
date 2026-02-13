package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopProductDto {
    private Long productId;
    private String productName;
    private long unitsSold;
    private double totalRevenue;
}
