package com.CommunityMarketPlace.New.dto;

import lombok.Data;
import java.util.List;

@Data
public class SellerDashboardDto {

    private double totalRevenue;
    private long totalOrders;

    private long pendingOrders;
    private long confirmedOrders;
    private long packedOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    private double todayRevenue;
    private double thisMonthRevenue;

    private List<MonthlySalesDto> last6MonthsRevenue;
    private List<TopProductDto> topProducts;

}
