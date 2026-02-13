package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.*;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addToCart(AddToCartRequest request);

    CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long itemId);

    CartResponse clearCart();
}
