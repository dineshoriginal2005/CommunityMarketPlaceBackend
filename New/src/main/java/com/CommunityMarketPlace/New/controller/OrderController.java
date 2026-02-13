package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.BuyNowRequest;
import com.CommunityMarketPlace.New.dto.OrderResponse;
import com.CommunityMarketPlace.New.dto.PlaceOrderRequest;
import com.CommunityMarketPlace.New.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;


    // POST /orders/place
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(@RequestBody PlaceOrderRequest request) {
        return ResponseEntity.ok(orderService.placeOrderFromCart(request));
    }

    // GET /orders
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        return ResponseEntity.ok(orderService.getMyOrders());
    }
    @PostMapping("/buy-now")
    public ResponseEntity<OrderResponse> buyNow(@RequestBody BuyNowRequest request) {
        return ResponseEntity.ok(orderService.buyNow(request));
    }


    // GET /orders/{orderId}
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getMyOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getMyOrderById(orderId));
    }

    // POST /orders/{orderId}/cancel
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelMyOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelMyOrder(orderId));
    }
}
