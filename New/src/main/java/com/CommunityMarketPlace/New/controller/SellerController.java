package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.model.Seller;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    // ---------------------------
    // CREATE SELLER PROFILE
    // ---------------------------
    @PostMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Seller> createSellerProfile(
            @RequestBody Seller seller,
            Authentication authentication) {

        User loggedInUser = (User) authentication.getPrincipal();

        // enforce seller -> user mapping
        seller.setUser(loggedInUser);
        seller.setId(loggedInUser.getId());

        // enforce initial status
        seller.setStatus("PENDING");

        return ResponseEntity.ok(sellerService.createSellerProfile(seller));
    }


    // ---------------------------
    // GET LOGGED-IN SELLER PROFILE
    // ---------------------------
    @GetMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Seller> getMyProfile(Authentication authentication) {

        User loggedInUser = (User) authentication.getPrincipal();
        Long sellerId = loggedInUser.getId();

        return ResponseEntity.ok(sellerService.getSellerById(sellerId));
    }


    // ---------------------------
    // UPDATE SELLER PROFILE
    // ---------------------------
    @PutMapping("/profile")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Seller> updateSellerProfile(
            @RequestBody Seller seller,
            Authentication authentication) {

        User loggedInUser = (User) authentication.getPrincipal();

        // enforce correct mapping
        seller.setUser(loggedInUser);
        seller.setId(loggedInUser.getId());

        // DO NOT allow seller to change status (admin only)
        Seller existing = sellerService.getSellerById(loggedInUser.getId());
        seller.setStatus(existing.getStatus());

        return ResponseEntity.ok(sellerService.updateSellerProfile(seller));
    }
}
