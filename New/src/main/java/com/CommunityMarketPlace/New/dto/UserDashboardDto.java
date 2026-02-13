package com.CommunityMarketPlace.New.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserDashboardDto {

    // Basic profile
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    // Order summary
    private long totalOrders;
    private long pendingOrders;
    private long shippedOrders;
    private long deliveredOrders;
    private long cancelledOrders;

    // Payment summary
    private double totalSpent;
    private double onlinePaidAmount;
    private double codAmount;

    // Last 5 orders
    private List<UserLastOrderDto> lastOrders;
}
