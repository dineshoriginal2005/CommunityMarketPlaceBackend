package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.SellerDashboardDto;
import com.CommunityMarketPlace.New.service.SellerOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/seller")
public class SellerDashboardController {
    @Autowired
    private SellerOrderService sellerOrderService;

        @GetMapping("/dashboard")
        public ResponseEntity<SellerDashboardDto> getDashboard() {
            return ResponseEntity.ok(sellerOrderService.getSellerDashboard());
        }
    }
