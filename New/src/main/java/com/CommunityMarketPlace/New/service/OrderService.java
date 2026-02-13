package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.BuyNowRequest;
import com.CommunityMarketPlace.New.dto.OrderResponse;
import com.CommunityMarketPlace.New.dto.PlaceOrderRequest;

import java.util.List;

public interface OrderService {

    OrderResponse placeOrderFromCart(PlaceOrderRequest request);

    List<OrderResponse> getMyOrders();
    OrderResponse buyNow(BuyNowRequest request);


    OrderResponse getMyOrderById(Long orderId);

    OrderResponse cancelMyOrder(Long orderId);
}
