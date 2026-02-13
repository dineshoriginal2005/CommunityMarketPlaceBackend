package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.OrderItem;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.repository.OrderItemRepository;
import com.CommunityMarketPlace.New.repository.OrderRepository;
import com.CommunityMarketPlace.New.service.SellerOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    // ----------------------------------------------------
    // Get current SELLER
    // ----------------------------------------------------
    private User getCurrentSeller() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }
        User seller = (User) principal;

        if (!"SELLER".equalsIgnoreCase(seller.getRole())) {
            throw new RuntimeException("Not a seller");
        }

        return seller;
    }

    // ----------------------------------------------------
    // GET ALL ORDERS THAT CONTAIN THIS SELLER'S PRODUCTS
    // ----------------------------------------------------
    @Override
    public List<SellerOrderResponse> getMySellerOrders() {

        User seller = getCurrentSeller();
        Long sellerId = seller.getId();

        List<OrderItem> items = orderItemRepository.findBySellerId(sellerId);

        // Group items by OrderID
        Map<Long, List<OrderItem>> ordersMap = new LinkedHashMap<>();

        for (OrderItem item : items) {
            ordersMap.computeIfAbsent(item.getOrder().getId(), k -> new ArrayList<>())
                    .add(item);
        }

        List<SellerOrderResponse> result = new ArrayList<>();

        for (Map.Entry<Long, List<OrderItem>> entry : ordersMap.entrySet()) {

            Long orderId = entry.getKey();
            List<OrderItem> orderItems = entry.getValue();
            Order order = orderItems.get(0).getOrder();

            SellerOrderResponse dto = new SellerOrderResponse();
            dto.setOrderId(orderId);
            dto.setBuyerName(order.getUser().getName());
            dto.setBuyerEmail(order.getUser().getEmail());
            dto.setOrderStatus(order.getOrderStatus());
            dto.setPaymentStatus(order.getPaymentStatus());
            dto.setPaymentMethod(order.getPaymentMethod());

            double sellerTotalAmount = 0;
            int sellerTotalItems = 0;

            List<OrderItemResponse> itemDtos = new ArrayList<>();

            for (OrderItem oi : orderItems) {
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
                itemDtos.add(r);

                sellerTotalAmount += oi.getTotalPrice();
                sellerTotalItems += oi.getQuantity();
            }

            dto.setSellerTotalAmount(sellerTotalAmount);
            dto.setSellerTotalItems(sellerTotalItems);
            dto.setItems(itemDtos);

            result.add(dto);
        }

        return result;
    }
    // ----------------------------------------------------
// SELLER DASHBOARD ANALYTICS (Full Option B)
// ----------------------------------------------------
    @Override
    public SellerDashboardDto getSellerDashboard() {

        User seller = getCurrentSeller();
        Long sellerId = seller.getId();

        List<OrderItem> items = orderItemRepository.findBySellerId(sellerId);

        SellerDashboardDto dto = new SellerDashboardDto();

        // TOTAL ORDERS (unique order IDs)
        dto.setTotalOrders(
                items.stream()
                        .map(i -> i.getOrder().getId())
                        .distinct()
                        .count()
        );

        // TOTAL REVENUE (only delivered items)
        dto.setTotalRevenue(
                items.stream()
                        .filter(i -> i.getItemStatus().equalsIgnoreCase("DELIVERED"))
                        .mapToDouble(OrderItem::getTotalPrice)
                        .sum()
        );

        // ORDER STATUS COUNTS
        dto.setPendingOrders(items.stream().filter(i -> i.getItemStatus().equals("PENDING")).count());
        dto.setConfirmedOrders(items.stream().filter(i -> i.getItemStatus().equals("CONFIRMED")).count());
        dto.setPackedOrders(items.stream().filter(i -> i.getItemStatus().equals("PACKED")).count());
        dto.setShippedOrders(items.stream().filter(i -> i.getItemStatus().equals("SHIPPED")).count());
        dto.setDeliveredOrders(items.stream().filter(i -> i.getItemStatus().equals("DELIVERED")).count());
        dto.setCancelledOrders(items.stream().filter(i -> i.getItemStatus().equals("CANCELLED")).count());

        // TODAY'S REVENUE
        java.time.LocalDate today = java.time.LocalDate.now();
        dto.setTodayRevenue(
                items.stream()
                        .filter(i -> i.getItemStatus().equals("DELIVERED"))
                        .filter(i -> {
                            java.time.LocalDate deliveredDate =
                                    java.time.Instant.ofEpochMilli(i.getOrder().getUpdatedAt())
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toLocalDate();
                            return deliveredDate.equals(today);
                        })
                        .mapToDouble(OrderItem::getTotalPrice)
                        .sum()
        );

        // THIS MONTH REVENUE
        YearMonth currentMonth = YearMonth.now();
        dto.setThisMonthRevenue(
                items.stream()
                        .filter(i -> i.getItemStatus().equals("DELIVERED"))
                        .filter(i -> {
                            java.time.LocalDate deliveredDate =
                                    java.time.Instant.ofEpochMilli(i.getOrder().getUpdatedAt())
                                            .atZone(java.time.ZoneId.systemDefault())
                                            .toLocalDate();
                            return deliveredDate.getYear() == currentMonth.getYear() &&
                                    deliveredDate.getMonthValue() == currentMonth.getMonthValue();
                        })
                        .mapToDouble(OrderItem::getTotalPrice)
                        .sum()
        );

        // LAST 6 MONTHS REVENUE
        List<MonthlySalesDto> monthly = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            YearMonth ym = currentMonth.minusMonths(i);
            double revenue = items.stream()
                    .filter(it -> it.getItemStatus().equals("DELIVERED"))
                    .filter(it -> {
                        java.time.LocalDate d =
                                java.time.Instant.ofEpochMilli(it.getOrder().getUpdatedAt())
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate();
                        return d.getYear() == ym.getYear() && d.getMonthValue() == ym.getMonthValue();
                    })
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();

            monthly.add(new MonthlySalesDto(ym.toString(), revenue));
        }
        dto.setLast6MonthsRevenue(monthly);

        // TOP PRODUCTS (by units sold)
        Map<Long, List<OrderItem>> grouped =
                items.stream()
                        .filter(i -> i.getItemStatus().equals("DELIVERED"))
                        .collect(Collectors.groupingBy(OrderItem::getProductId));

        List<TopProductDto> topProducts = grouped.entrySet().stream()
                .map(e -> {
                    long units = e.getValue().stream().mapToLong(OrderItem::getQuantity).sum();
                    double revenue = e.getValue().stream().mapToDouble(OrderItem::getTotalPrice).sum();
                    String name = e.getValue().get(0).getProductName();
                    return new TopProductDto(e.getKey(), name, units, revenue);
                })
                .sorted((a, b) -> Long.compare(b.getUnitsSold(), a.getUnitsSold()))
                .limit(5)
                .collect(Collectors.toList());

        dto.setTopProducts(topProducts);

        return dto;
    }

    // ----------------------------------------------------
    // UPDATE ITEM STATUS (CONFIRMED, PACKED, SHIPPED, DELIVERED)
    // ----------------------------------------------------
    @Override
    public SellerOrderResponse updateOrderItemStatus(Long orderItemId, UpdateOrderItemStatusRequest request) {

        User seller = getCurrentSeller();
        Long sellerId = seller.getId();

        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        if (!item.getSellerId().equals(sellerId)) {
            throw new RuntimeException("Cannot modify item of another seller");
        }

        // -----------------------------
        // 1️⃣ Update item status
        // -----------------------------
        item.setItemStatus(request.getStatus());
        orderItemRepository.save(item);

        // -----------------------------
        // 2️⃣ Load full order
        // -----------------------------
        Order order = item.getOrder();
        List<OrderItem> allItems = order.getItems();

        // -----------------------------
        // 3️⃣ Determine NEW orderStatus
        // -----------------------------
        boolean allDelivered = allItems.stream()
                .allMatch(i -> i.getItemStatus().equalsIgnoreCase("DELIVERED"));

        boolean anyShipped = allItems.stream()
                .anyMatch(i -> i.getItemStatus().equalsIgnoreCase("SHIPPED"));

        boolean anyPacked = allItems.stream()
                .anyMatch(i -> i.getItemStatus().equalsIgnoreCase("PACKED"));

        boolean anyConfirmed = allItems.stream()
                .anyMatch(i -> i.getItemStatus().equalsIgnoreCase("CONFIRMED"));

        String newOrderStatus;

        if (allDelivered) {
            newOrderStatus = "DELIVERED";
        } else if (anyShipped) {
            newOrderStatus = "SHIPPED";
        } else if (anyPacked) {
            newOrderStatus = "PACKED";
        } else if (anyConfirmed) {
            newOrderStatus = "CONFIRMED";
        } else {
            newOrderStatus = "PENDING";
        }

        order.setOrderStatus(newOrderStatus);

        // -----------------------------
        // 4️⃣ Auto-payment update (COD only)
        // -----------------------------
        if (newOrderStatus.equalsIgnoreCase("DELIVERED")
                && order.getPaymentMethod().equalsIgnoreCase("COD")) {

            order.setPaymentStatus("PAID");
        }

        orderRepository.save(order);

        // -----------------------------
        // 5️⃣ Build response FOR THIS SELLER ONLY
        // -----------------------------
        List<OrderItem> sellerItems = orderItemRepository.findBySellerId(sellerId);

        List<OrderItem> orderItemsForThisSeller = new ArrayList<>();
        for (OrderItem oi : sellerItems) {
            if (oi.getOrder().getId().equals(order.getId())) {
                orderItemsForThisSeller.add(oi);
            }
        }

        List<OrderItemResponse> itemDtos = new ArrayList<>();
        double sellerTotalAmount = 0.0;
        int sellerTotalItems = 0;

        for (OrderItem oi : orderItemsForThisSeller) {
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
            itemDtos.add(r);

            sellerTotalAmount += oi.getTotalPrice();
            sellerTotalItems += oi.getQuantity();
        }

        SellerOrderResponse dto = new SellerOrderResponse();
        dto.setOrderId(order.getId());
        dto.setBuyerName(order.getUser().getName());
        dto.setBuyerEmail(order.getUser().getEmail());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setSellerTotalAmount(sellerTotalAmount);
        dto.setSellerTotalItems(sellerTotalItems);
        dto.setItems(itemDtos);

        return dto;
    }
}
