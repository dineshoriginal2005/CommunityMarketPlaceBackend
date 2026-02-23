package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // User: get items inside an order
    List<OrderItem> findByOrder(Order order);

    // Seller: get all items created by a seller
    List<OrderItem> findBySellerId(Long sellerId);

    // Admin: get all items for given orderId
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findBySellerIdAndOrderId(Long sellerId, Long orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.sellerId = :sellerId")
    List<OrderItem> findAllBySellerId(@Param("sellerId") Long sellerId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.sellerId = :sellerId AND oi.itemStatus = 'DELIVERED'")
    List<OrderItem> findDeliveredBySeller(@Param("sellerId") Long sellerId);

}
