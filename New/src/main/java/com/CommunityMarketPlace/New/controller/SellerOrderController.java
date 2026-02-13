package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.SellerOrderResponse;
import com.CommunityMarketPlace.New.dto.UpdateOrderItemStatusRequest;
import com.CommunityMarketPlace.New.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/seller/orders")
public class SellerOrderController {

    @Autowired
    private SellerOrderService sellerOrderService;


    // GET /seller/orders
    @GetMapping
    public ResponseEntity<List<SellerOrderResponse>> getMySellerOrders() {
        return ResponseEntity.ok(sellerOrderService.getMySellerOrders());
    }

    // PUT /seller/orders/items/{orderItemId}/status
    @PutMapping("/items/{orderItemId}/status")
    public ResponseEntity<SellerOrderResponse> updateOrderItemStatus(
            @PathVariable Long orderItemId,
            @RequestBody UpdateOrderItemStatusRequest request) {

        return ResponseEntity.ok(sellerOrderService.updateOrderItemStatus(orderItemId, request));
    }
}
