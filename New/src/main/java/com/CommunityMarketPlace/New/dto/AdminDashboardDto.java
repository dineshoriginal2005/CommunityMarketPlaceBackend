package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class AdminDashboardDto {
    private long totalUsers;
    private long totalSellers;
    private long pendingSellers;

    private long totalOrders;
    private long pendingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    private double totalRevenue;        // PAID only
    private double codPendingAmount;    // COD + PENDING
    private double onlinePaidAmount;    // NON-COD + PAID
}
