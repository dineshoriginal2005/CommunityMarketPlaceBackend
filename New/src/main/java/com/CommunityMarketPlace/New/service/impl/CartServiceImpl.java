package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.*;
import com.CommunityMarketPlace.New.repository.*;
import com.CommunityMarketPlace.New.service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication");
        }
        return (User) principal;
    }


    @Override
    public CartResponse getMyCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        return mapToResponse(cart);
    }


    @Override
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new RuntimeException("Product is inactive");
        }

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product).orElse(null);

        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(request.getQuantity());
            cart.getItems().add(item);
        } else {
            item.setQuantity(item.getQuantity() + request.getQuantity());
        }

        cartRepository.save(cart);
        return mapToResponse(cart);
    }


    @Override
    public CartResponse updateCartItem(Long itemId, UpdateCartItemRequest request) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Access denied");
        }

        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);

        return mapToResponse(cart);
    }


    @Override
    public CartResponse removeCartItem(Long itemId) {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Access denied");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return mapToResponse(cart);
    }


    @Override
    public CartResponse clearCart() {
        User user = getCurrentUser();
        Cart cart = getOrCreateCart(user);

        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToResponse(cart);
    }


    // ---------- PRIVATE HELPERS ----------

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }


    private CartResponse mapToResponse(Cart cart) {

        List<CartItemResponse> items = cart.getItems().stream().map(ci -> {
            CartItemResponse r = new CartItemResponse();
            r.setItemId(ci.getId());
            r.setProductId(ci.getProduct().getId());
            r.setProductName(ci.getProduct().getName());
            r.setImageUrl(ci.getProduct().getImageUrl());
            r.setPrice(ci.getProduct().getPrice());
            r.setQuantity(ci.getQuantity());
            r.setTotalPrice(ci.getProduct().getPrice() * ci.getQuantity());
            return r;
        }).toList();

        CartResponse response = new CartResponse();
        response.setItems(items);

        double total = items.stream()
                .mapToDouble(CartItemResponse::getTotalPrice)
                .sum();

        int quantity = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        response.setTotalAmount(total);
        response.setTotalQuantity(quantity);

        return response;
    }
}
