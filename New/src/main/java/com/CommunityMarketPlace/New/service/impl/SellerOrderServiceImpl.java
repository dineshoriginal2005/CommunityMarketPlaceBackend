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

    private User getCurrentSeller() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof User)) throw new RuntimeException("Invalid authentication principal");
        User seller = (User) principal;
        if (!"SELLER".equalsIgnoreCase(seller.getRole())) throw new RuntimeException("Not a seller");
        return seller;
    }

    @Override
    public List<SellerOrderResponse> getMySellerOrders() {
        User seller = getCurrentSeller();
        List<OrderItem> items = orderItemRepository.findBySellerId(seller.getId());

        Map<Long, List<OrderItem>> ordersMap = new LinkedHashMap<>();
        for (OrderItem item : items) {
            ordersMap.computeIfAbsent(item.getOrder().getId(), k -> new ArrayList<>()).add(item);
        }

        return ordersMap.values().stream()
                .map(orderItems -> mapToSellerOrderResponse(orderItems.get(0).getOrder(), orderItems))
                .collect(Collectors.toList());
    }

    @Override
    public SellerOrderResponse updateOrderItemStatus(Long orderItemId, UpdateOrderItemStatusRequest request) {
        User seller = getCurrentSeller();
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        if (!item.getSellerId().equals(seller.getId())) {
            throw new RuntimeException("Cannot modify item of another seller");
        }

        // 1. Update the Item
        item.setItemStatus(request.getStatus().toUpperCase());
        orderItemRepository.save(item);

        // 2. Determine Order Status (Ignoring CANCELLED items)
        Order order = item.getOrder();
        List<OrderItem> allItems = order.getItems();

        // Filter out cancelled items to check if the 'active' part of the order is done
        List<OrderItem> activeItems = allItems.stream()
                .filter(i -> !i.getItemStatus().equalsIgnoreCase("CANCELLED"))
                .collect(Collectors.toList());

        String newStatus;
        if (activeItems.isEmpty()) {
            newStatus = "CANCELLED";
        } else if (activeItems.stream().allMatch(i -> i.getItemStatus().equalsIgnoreCase("DELIVERED"))) {
            newStatus = "DELIVERED";
        } else if (activeItems.stream().anyMatch(i -> i.getItemStatus().equalsIgnoreCase("SHIPPED"))) {
            newStatus = "SHIPPED";
        } else if (activeItems.stream().anyMatch(i -> i.getItemStatus().equalsIgnoreCase("PACKED"))) {
            newStatus = "PACKED";
        } else if (activeItems.stream().anyMatch(i -> i.getItemStatus().equalsIgnoreCase("CONFIRMED"))) {
            newStatus = "CONFIRMED";
        } else {
            newStatus = "PENDING";
        }

        order.setOrderStatus(newStatus);

        // 3. Auto-payment for COD and Online Payment logic
        if ("DELIVERED".equalsIgnoreCase(newStatus)) {
            if ("COD".equalsIgnoreCase(order.getPaymentMethod())) {
                order.setPaymentStatus("PAID");
            } else if ("ONLINE".equalsIgnoreCase(order.getPaymentMethod())) {
                // For online payments, it should ideally be PAID already, but ensure it is marked PAID
                // when the order is successfully delivered.
                order.setPaymentStatus("PAID");
            }
        }
        
        orderRepository.save(order);

        // 4. Return response for THIS seller's items in THIS order only
        List<OrderItem> sellerItemsInOrder = orderItemRepository.findBySellerIdAndOrderId(seller.getId(), order.getId());
        return mapToSellerOrderResponse(order, sellerItemsInOrder);
    }

    @Override
    public SellerDashboardDto getSellerDashboard() {
        User seller = getCurrentSeller();
        List<OrderItem> items = orderItemRepository.findBySellerId(seller.getId());
        SellerDashboardDto dto = new SellerDashboardDto();

        // 1. Basic Stats & One-Pass Revenue Calculation
        dto.setTotalOrders(items.stream().map(i -> i.getOrder().getId()).distinct().count());

        double totalRev = 0, todayRev = 0, monthRev = 0;
        long p=0, c=0, pk=0, s=0, d=0, can=0;

        java.time.LocalDate today = java.time.LocalDate.now();
        YearMonth thisMonth = YearMonth.now();

        for (OrderItem i : items) {
            String status = i.getItemStatus().toUpperCase();
            // Count statuses
            switch(status) {
                case "PENDING": p++; break;
                case "CONFIRMED": c++; break;
                case "PACKED": pk++; break;
                case "SHIPPED": s++; break;
                case "DELIVERED": d++; break;
                case "CANCELLED": can++; break;
            }

            // Calculate Revenue only for items that THIS seller delivered
            if ("DELIVERED".equals(status)) {
                double price = i.getTotalPrice();
                totalRev += price;
                java.time.LocalDate date = java.time.Instant.ofEpochMilli(i.getOrder().getUpdatedAt())
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                if (date.equals(today)) todayRev += price;
                if (YearMonth.from(date).equals(thisMonth)) monthRev += price;
            }
        }

        dto.setTotalRevenue(totalRev);
        dto.setTodayRevenue(todayRev);
        dto.setThisMonthRevenue(monthRev);
        dto.setPendingOrders(p); dto.setConfirmedOrders(c);
        dto.setPackedOrders(pk); dto.setShippedOrders(s);
        dto.setDeliveredOrders(d); dto.setCancelledOrders(can);

        // 2. Last 6 Months Revenue Logic
        List<MonthlySalesDto> last6Months = new ArrayList<>();
        for (int j = 5; j >= 0; j--) {
            YearMonth targetMonth = thisMonth.minusMonths(j);
            double monthlyRev = items.stream()
                    .filter(i -> "DELIVERED".equalsIgnoreCase(i.getItemStatus()))
                    .filter(i -> {
                        java.time.LocalDate date = java.time.Instant.ofEpochMilli(i.getOrder().getUpdatedAt())
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                        return YearMonth.from(date).equals(targetMonth);
                    })
                    .mapToDouble(OrderItem::getTotalPrice)
                    .sum();
            last6Months.add(new MonthlySalesDto(targetMonth.toString(), monthlyRev));
        }
        dto.setLast6MonthsRevenue(last6Months);

        // 3. Top Products (By Delivered Units)
        dto.setTopProducts(items.stream()
                .filter(i -> "DELIVERED".equalsIgnoreCase(i.getItemStatus()))
                .collect(Collectors.groupingBy(OrderItem::getProductId))
                .entrySet().stream()
                .map(e -> new TopProductDto(
                        e.getKey(),
                        e.getValue().get(0).getProductName(),
                        e.getValue().stream().mapToLong(OrderItem::getQuantity).sum(),
                        e.getValue().stream().mapToDouble(OrderItem::getTotalPrice).sum()))
                .sorted((a, b) -> Long.compare(b.getUnitsSold(), a.getUnitsSold()))
                .limit(5).collect(Collectors.toList()));

        return dto;
    }
    private SellerOrderResponse mapToSellerOrderResponse(Order order, List<OrderItem> items) {
        SellerOrderResponse dto = new SellerOrderResponse();
        dto.setOrderId(order.getId());
        dto.setBuyerName(order.getUser().getName());
        dto.setBuyerEmail(order.getUser().getEmail());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());

        List<OrderItemResponse> itemDtos = items.stream().map(oi -> {
            OrderItemResponse r = new OrderItemResponse();
            r.setOrderItemId(oi.getId());
            r.setProductId(oi.getProductId());
            r.setProductName(oi.getProductName());
            r.setProductImage(oi.getProductImage());
            r.setQuantity(oi.getQuantity());
            r.setPriceAtPurchase(oi.getPriceAtPurchase());
            r.setTotalPrice(oi.getTotalPrice());
            r.setItemStatus(oi.getItemStatus());
            return r;
        }).collect(Collectors.toList());

        dto.setItems(itemDtos);
        dto.setSellerTotalAmount(items.stream().mapToDouble(OrderItem::getTotalPrice).sum());
        dto.setSellerTotalItems(items.stream().mapToInt(OrderItem::getQuantity).sum());
        return dto;
    }
}