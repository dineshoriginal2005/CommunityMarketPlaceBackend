package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.model.Seller;
import com.CommunityMarketPlace.New.repository.SellerRepository;
import com.CommunityMarketPlace.New.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;


    // --------------------------------------------------------
    // CHECK IF SELLER PROFILE ALREADY EXISTS
    // --------------------------------------------------------
    @Override
    public boolean sellerExists(Long userId) {
        return sellerRepository.existsById(userId);
    }


    // --------------------------------------------------------
    // CREATE SELLER PROFILE
    // --------------------------------------------------------
    @Override
    public Seller createSellerProfile(Seller seller) {

        // Prevent duplicate seller profile creation
        if (sellerRepository.existsById(seller.getId())) {
            throw new RuntimeException("Seller profile already exists");
        }

        seller.setStatus("PENDING"); // Always enforce default status

        return sellerRepository.save(seller);
    }


    // --------------------------------------------------------
    // GET SELLER BY USER ID (sellerId same as userId)
    // --------------------------------------------------------
    @Override
    public Seller getSellerById(Long sellerId) {
        return sellerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));
    }


    // --------------------------------------------------------
    // UPDATE SELLER PROFILE (SAFE UPDATE)
    // --------------------------------------------------------
    @Override
    public Seller updateSellerProfile(Seller seller) {

        Seller existing = sellerRepository.findById(seller.getId())
                .orElseThrow(() -> new RuntimeException("Seller profile not found"));

        // Allow only editable fields
        existing.setBusinessName(seller.getBusinessName());
        existing.setGstNumber(seller.getGstNumber());
        existing.setShopAddress(seller.getShopAddress());
        existing.setBusinessPhone(seller.getBusinessPhone());

        // Keep admin-controlled fields unchanged
        existing.setStatus(existing.getStatus());
        existing.setUser(existing.getUser());

        return sellerRepository.save(existing);
    }
}
