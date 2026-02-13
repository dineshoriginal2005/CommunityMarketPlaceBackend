package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // USER SIDE
    List<Order> findByUser(User user);
    Optional<Order> findByIdAndUser(Long id, User user);

    // ADMIN SIDE
    List<Order> findByUserId(Long userId);
    List<Order> findByOrderStatusIgnoreCase(String status);

    // PAYMENT LOOKUP (IMPORTANT FOR ONLINE PAYMENTS)
    Optional<Order> findByPayments_RazorpayOrderId(String razorpayOrderId);
}
