package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.model.Seller;

public interface SellerService {

    Seller createSellerProfile(Seller seller);

    Seller getSellerById(Long sellerId);

    Seller updateSellerProfile(Seller seller);

    boolean sellerExists(Long userId);
}
