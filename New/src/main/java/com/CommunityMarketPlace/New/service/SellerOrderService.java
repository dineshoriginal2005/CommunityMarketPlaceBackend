package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.SellerDashboardDto;
import com.CommunityMarketPlace.New.dto.SellerOrderResponse;
import com.CommunityMarketPlace.New.dto.UpdateOrderItemStatusRequest;

import java.util.List;

public interface SellerOrderService {

    List<SellerOrderResponse> getMySellerOrders();
    SellerDashboardDto getSellerDashboard();


    SellerOrderResponse updateOrderItemStatus(Long orderItemId, UpdateOrderItemStatusRequest request);
}
