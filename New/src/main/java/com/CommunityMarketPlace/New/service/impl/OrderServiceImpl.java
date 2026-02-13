package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.*;
import com.CommunityMarketPlace.New.repository.*;
import com.CommunityMarketPlace.New.service.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }
        return (User) principal;
    }

    // ============================== PLACE ORDER FROM CART ==============================
    @Override
    public OrderResponse placeOrderFromCart(PlaceOrderRequest request) {

        User user = getCurrentUser();

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        List<CartItem> cartItems = new ArrayList<>(cart.getItems());
        List<OrderItem> orderItems = new ArrayList<>();

        double totalAmount = 0.0;
        int totalItems = 0;

        for (CartItem ci : cartItems) {

            Product product = ci.getProduct();

            if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
                throw new RuntimeException("Product " + product.getName() + " is not available");
            }

            if (product.getQuantity() < ci.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            product.setQuantity(product.getQuantity() - ci.getQuantity());
            productRepository.save(product);

            double itemTotal = product.getPrice() * ci.getQuantity();
            totalAmount += itemTotal;
            totalItems += ci.getQuantity();

            OrderItem oi = new OrderItem();
            oi.setProductId(product.getId());
            oi.setProductName(product.getName());
            oi.setProductImage(product.getImageUrl());
            oi.setSellerId(product.getSellerId());
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtPurchase(product.getPrice());
            oi.setTotalPrice(itemTotal);
            oi.setItemStatus("PENDING");

            orderItems.add(oi);
        }

        long now = System.currentTimeMillis();

        Order order = new Order();
        order.setUser(user);

        String method = request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD";
        order.setPaymentMethod(method);

        if (!method.equalsIgnoreCase("COD")) {
            order.setOrderStatus("PENDING_PAYMENT");
            order.setPaymentStatus("NOT_PAID");
        } else {
            order.setOrderStatus("CONFIRMED");
            order.setPaymentStatus("PAID");
        }

        order.setTotalAmount(totalAmount);
        order.setTotalItems(totalItems);

        order.setShippingFullName(user.getFullName());
        order.setShippingPhone(user.getPhoneNumber());
        order.setShippingAddressLine1(user.getAddressLine1());
        order.setShippingAddressLine2(user.getAddressLine2());
        order.setShippingCity(user.getCity());
        order.setShippingState(user.getState());
        order.setShippingPincode(user.getPincode());
        order.setShippingCountry(user.getCountry());

        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        for (OrderItem oi : orderItems) {
            oi.setOrder(order);
        }
        order.setItems(orderItems);

        orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return mapToOrderResponse(order);
    }

    // ============================== GET MY ORDERS ==============================
    @Override
    public List<OrderResponse> getMyOrders() {
        User user = getCurrentUser();
        return orderRepository.findByUser(user)
                .stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    // ============================== GET ORDER BY ID ==============================
    @Override
    public OrderResponse getMyOrderById(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return mapToOrderResponse(order);
    }

    // ============================== BUY NOW ==============================
    @Override
    public OrderResponse buyNow(BuyNowRequest request) {

        User user = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!"ACTIVE".equalsIgnoreCase(product.getStatus())) {
            throw new RuntimeException("Product is not available");
        }

        if (product.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setQuantity(product.getQuantity() - request.getQuantity());
        productRepository.save(product);

        double itemTotal = product.getPrice() * request.getQuantity();
        long now = System.currentTimeMillis();

        Order order = new Order();
        order.setUser(user);

        String method = request.getPaymentMethod() != null ? request.getPaymentMethod() : "COD";
        order.setPaymentMethod(method);

        if (!method.equalsIgnoreCase("COD")) {
            order.setOrderStatus("PENDING_PAYMENT");
            order.setPaymentStatus("NOT_PAID");
        } else {
            order.setOrderStatus("CONFIRMED");
            order.setPaymentStatus("PAID");
        }

        order.setTotalAmount(itemTotal);
        order.setTotalItems(request.getQuantity());

        order.setShippingFullName(user.getFullName());
        order.setShippingPhone(user.getPhoneNumber());
        order.setShippingAddressLine1(user.getAddressLine1());
        order.setShippingAddressLine2(user.getAddressLine2());
        order.setShippingCity(user.getCity());
        order.setShippingState(user.getState());
        order.setShippingPincode(user.getPincode());
        order.setShippingCountry(user.getCountry());

        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        OrderItem oi = new OrderItem();
        oi.setOrder(order);
        oi.setProductId(product.getId());
        oi.setProductName(product.getName());
        oi.setProductImage(product.getImageUrl());
        oi.setSellerId(product.getSellerId());
        oi.setQuantity(request.getQuantity());
        oi.setPriceAtPurchase(product.getPrice());
        oi.setTotalPrice(itemTotal);
        oi.setItemStatus("PENDING");

        order.setItems(List.of(oi));

        orderRepository.save(order);

        return mapToOrderResponse(order);
    }

    // ============================== CANCEL ORDER ==============================
    @Override
    public OrderResponse cancelMyOrder(Long orderId) {
        User user = getCurrentUser();
        Order order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("SHIPPED".equalsIgnoreCase(order.getOrderStatus()) ||
                "DELIVERED".equalsIgnoreCase(order.getOrderStatus())) {
            throw new RuntimeException("Cannot cancel shipped/delivered order");
        }

        order.setOrderStatus("CANCELLED");
        order.setUpdatedAt(System.currentTimeMillis());

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
            item.setItemStatus("CANCELLED");
        }

        orderRepository.save(order);
        return mapToOrderResponse(order);
    }

    // ============================== MAPPER ==============================
    private OrderResponse mapToOrderResponse(Order order) {

        OrderResponse dto = new OrderResponse();

        dto.setOrderId(order.getId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTotalItems(order.getTotalItems());

        dto.setShippingFullName(order.getShippingFullName());
        dto.setShippingPhone(order.getShippingPhone());
        dto.setShippingAddressLine1(order.getShippingAddressLine1());
        dto.setShippingAddressLine2(order.getShippingAddressLine2());
        dto.setShippingCity(order.getShippingCity());
        dto.setShippingState(order.getShippingState());
        dto.setShippingPincode(order.getShippingPincode());
        dto.setShippingCountry(order.getShippingCountry());

        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        dto.setItems(
                order.getItems().stream().map(oi -> {
                    OrderItemResponse r = new OrderItemResponse();
                    r.setOrderItemId(oi.getId());
                    r.setProductId(oi.getProductId());
                    r.setProductName(oi.getProductName());
                    r.setProductImage(oi.getProductImage());
                    r.setSellerId(oi.getSellerId());
                    r.setQuantity(oi.getQuantity());
                    r.setPriceAtPurchase(oi.getPriceAtPurchase());
                    r.setTotalPrice(oi.getTotalPrice());
                    r.setItemStatus(oi.getItemStatus());
                    return r;
                }).toList()
        );

        dto.setPayments(
                order.getPayments() == null ? List.of()
                        : order.getPayments().stream().map(p -> {
                    PaymentDetailsResponse pr = new PaymentDetailsResponse();
                    pr.setId(p.getId());
                    pr.setRazorpayOrderId(p.getRazorpayOrderId());
                    pr.setRazorpayPaymentId(p.getRazorpayPaymentId());
                    pr.setRazorpaySignature(p.getRazorpaySignature());
                    pr.setAmount(p.getAmount());
                    pr.setCurrency(p.getCurrency());
                    pr.setStatus(p.getStatus());
                    pr.setCreatedAt(p.getCreatedAt());
                    pr.setUpdatedAt(p.getUpdatedAt());
                    return pr;
                }).toList()
        );

        return dto;
    }
}
