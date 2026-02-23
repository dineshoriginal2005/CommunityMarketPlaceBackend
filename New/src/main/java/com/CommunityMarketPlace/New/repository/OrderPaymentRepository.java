package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.OrderPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderPaymentRepository extends JpaRepository<OrderPayment, Long> {

    Optional<OrderPayment> findByRazorpayOrderId(String razorpayOrderId);

    // 🔥 ADD THIS
    Optional<OrderPayment> findByOrder(Order order);
}
