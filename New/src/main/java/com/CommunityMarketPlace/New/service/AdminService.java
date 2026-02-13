package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.Order;

import java.util.List;

public interface AdminService {

    // === Seller approval ===
    List<AdminSellerDto> getPendingSellers();
    AdminSellerDto approveSeller(Long sellerId);
    AdminSellerDto rejectSeller(Long sellerId);
    List<AdminSellerDto> getAllSellers();

    // === Users ===
    List<AdminUserDto> getAllUsers();
    AdminUserDto blockUser(Long userId);
    AdminUserDto unblockUser(Long userId);

    // === Orders ===
    List<AdminOrderDto> getAllOrders();
    AdminOrderDto getOrderById(Long orderId);
    List<AdminOrderDto> getOrdersByStatus(String status);
    List<AdminOrderDto> getOrdersByBuyer(Long userId);
    List<AdminOrderItemDto> getOrderItemsBySeller(Long sellerId);

    Order adminUpdateOrderStatus(Long orderId, String newStatus);
    Order adminUpdatePaymentStatus(Long orderId, String newPaymentStatus);

    // === Dashboard ===
    AdminDashboardDto getOrderDashboard();
}
