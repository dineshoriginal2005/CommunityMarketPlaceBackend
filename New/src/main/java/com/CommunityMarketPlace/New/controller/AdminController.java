package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ===== SELLER APPROVAL =====

    @GetMapping("/pending-sellers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminSellerDto>> getPendingSellers() {
        return ResponseEntity.ok(adminService.getPendingSellers());
    }

    @PostMapping("/approve-seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSellerDto> approveSeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.approveSeller(sellerId));
    }

    @PostMapping("/reject-seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminSellerDto> rejectSeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.rejectSeller(sellerId));
    }

    @GetMapping("/sellers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminSellerDto>> getAllSellers() {
        return ResponseEntity.ok(adminService.getAllSellers());
    }

    // ===== USERS =====

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminUserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/{userId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDto> blockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.blockUser(userId));
    }

    @PutMapping("/users/{userId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminUserDto> unblockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.unblockUser(userId));
    }

    // ===== ORDERS =====

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderDto>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminOrderDto> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(adminService.getOrderById(orderId));
    }

    @GetMapping("/orders/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderDto>> getOrdersByStatus(@PathVariable String status) {
        return ResponseEntity.ok(adminService.getOrdersByStatus(status));
    }

    @GetMapping("/orders/buyer/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderDto>> getOrdersByBuyer(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getOrdersByBuyer(userId));
    }

    @GetMapping("/orders/seller/{sellerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminOrderItemDto>> getOrdersBySeller(@PathVariable Long sellerId) {
        return ResponseEntity.ok(adminService.getOrderItemsBySeller(sellerId));
    }

    // ===== FIXED PUT ENDPOINTS (RETURN DTO INSTEAD OF ENTITY) =====

    @PutMapping("/orders/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminOrderDto> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody String newStatus) {

        Order updatedOrder = adminService.adminUpdateOrderStatus(orderId, newStatus);

        AdminOrderDto dto = adminService.getOrderById(orderId); // Convert to DTO
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/orders/{orderId}/payment-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminOrderDto> updatePaymentStatus(
            @PathVariable Long orderId,
            @RequestBody String newPaymentStatus) {

        Order updatedOrder = adminService.adminUpdatePaymentStatus(orderId, newPaymentStatus);

        AdminOrderDto dto = adminService.getOrderById(orderId); // Convert to DTO
        return ResponseEntity.ok(dto);
    }

    // ===== DASHBOARD =====

    @GetMapping("/orders/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDto> getDashboard() {
        return ResponseEntity.ok(adminService.getOrderDashboard());
    }
}
