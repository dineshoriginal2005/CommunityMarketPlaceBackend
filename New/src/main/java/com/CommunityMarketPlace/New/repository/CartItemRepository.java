package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Cart;
import com.CommunityMarketPlace.New.model.CartItem;
import com.CommunityMarketPlace.New.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
