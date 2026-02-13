package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MonthlySalesDto {
    private String month;   // "2025-01"
    private double amount;
}
