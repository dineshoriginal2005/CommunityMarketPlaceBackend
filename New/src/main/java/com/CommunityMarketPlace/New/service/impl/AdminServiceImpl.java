package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.OrderItem;
import com.CommunityMarketPlace.New.model.Seller;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.repository.*;
import com.CommunityMarketPlace.New.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // ================= INTERNAL HELPERS =================

    /**
     * Only for DISPLAY.
     * Never save this fallback object to DB.
     */
    private Seller buildDisplaySellerFallback(User user) {
        Seller s = new Seller();
        s.setId(user.getId());          // only for DTO mapping, not for persist
        s.setUser(user);
        s.setBusinessName("N/A");
        s.setGstNumber("N/A");
        s.setShopAddress("N/A");
        s.setBusinessPhone("N/A");
        s.setStatus("UNKNOWN");
        return s;
    }

    private AdminSellerDto mapSeller(User user, Seller seller) {
        AdminSellerDto dto = new AdminSellerDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setSellerVerified(user.isSellerVerified());

        if (seller == null) {
            seller = buildDisplaySellerFallback(user);
        }

        dto.setBusinessName(seller.getBusinessName());
        dto.setGstNumber(seller.getGstNumber());
        dto.setShopAddress(seller.getShopAddress());
        dto.setBusinessPhone(seller.getBusinessPhone());
        dto.setStatus(seller.getStatus());

        return dto;
    }

    private AdminUserDto mapUser(User user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setSellerVerified(user.isSellerVerified());
        dto.setBlocked(user.isBlocked());
        return dto;
    }

    private AdminOrderItemDto mapItem(OrderItem item) {
        AdminOrderItemDto dto = new AdminOrderItemDto();
        dto.setOrderItemId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setProductName(item.getProductName());
        dto.setProductImage(item.getProductImage());
        dto.setSellerId(item.getSellerId());
        dto.setQuantity(item.getQuantity());
        dto.setPriceAtPurchase(item.getPriceAtPurchase());
        dto.setTotalPrice(item.getTotalPrice());
        dto.setItemStatus(item.getItemStatus());
        return dto;
    }

    private AdminOrderDto mapOrder(Order order) {
        AdminOrderDto dto = new AdminOrderDto();

        dto.setOrderId(order.getId());
        dto.setUserId(order.getUser().getId());
        dto.setBuyerName(order.getUser().getName());
        dto.setBuyerEmail(order.getUser().getEmail());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setTotalItems(order.getTotalItems());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());

        List<AdminOrderItemDto> itemDtos = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            itemDtos.add(mapItem(item));
        }
        dto.setItems(itemDtos);

        return dto;
    }

    // ================= SELLER APPROVAL =================

    @Override
    public List<AdminSellerDto> getPendingSellers() {
        return userRepository.findAll().stream()
                .filter(u -> "SELLER".equalsIgnoreCase(u.getRole()) && !u.isSellerVerified())
                .map(u -> {
                    Seller seller = sellerRepository.findById(u.getId()).orElse(null);
                    return mapSeller(u, seller);
                })
                .toList();
    }

    @Override
    public AdminSellerDto approveSeller(Long sellerId) {
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller user not found"));

        // mark user as verified
        user.setSellerVerified(true);
        userRepository.save(user);

        // load existing seller profile or create a new one correctly
        Seller seller = sellerRepository.findById(sellerId).orElse(null);
        if (seller == null) {
            seller = new Seller();
            seller.setUser(user);    // IMPORTANT for @MapsId
            seller.setBusinessName("N/A");
            seller.setGstNumber("N/A");
            seller.setShopAddress("N/A");
            seller.setBusinessPhone("N/A");
        }
        seller.setStatus("APPROVED");
        sellerRepository.save(seller);

        return mapSeller(user, seller);
    }

    @Override
    public AdminSellerDto rejectSeller(Long sellerId) {
        User user = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller user not found"));

        // mark user as NOT verified
        user.setSellerVerified(false);
        userRepository.save(user);

        Seller seller = sellerRepository.findById(sellerId).orElse(null);
        if (seller == null) {
            seller = new Seller();
            seller.setUser(user);    // IMPORTANT for @MapsId
            seller.setBusinessName("N/A");
            seller.setGstNumber("N/A");
            seller.setShopAddress("N/A");
            seller.setBusinessPhone("N/A");
        }
        seller.setStatus("REJECTED");
        sellerRepository.save(seller);

        return mapSeller(user, seller);
    }

    @Override
    public List<AdminSellerDto> getAllSellers() {
        return userRepository.findAll().stream()
                .filter(u -> "SELLER".equalsIgnoreCase(u.getRole()))
                .map(u -> {
                    Seller seller = sellerRepository.findById(u.getId()).orElse(null);
                    return mapSeller(u, seller);
                })
                .toList();
    }

    // ================= USERS =================

    @Override
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public AdminUserDto blockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(true);
        userRepository.save(user);
        return mapUser(user);
    }

    @Override
    public AdminUserDto unblockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setBlocked(false);
        userRepository.save(user);
        return mapUser(user);
    }

    // ================= ORDERS =================

    @Override
    public List<AdminOrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapOrder)
                .toList();
    }

    @Override
    public AdminOrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .map(this::mapOrder)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Override
    public List<AdminOrderDto> getOrdersByStatus(String status) {
        return orderRepository.findByOrderStatusIgnoreCase(status)
                .stream()
                .map(this::mapOrder)
                .toList();
    }

    @Override
    public List<AdminOrderDto> getOrdersByBuyer(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapOrder)
                .toList();
    }

    @Override
    public List<AdminOrderItemDto> getOrderItemsBySeller(Long sellerId) {
        return orderItemRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapItem)
                .toList();
    }

    @Override
    public Order adminUpdateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        String status = newStatus.toUpperCase();
        order.setOrderStatus(status);

        // 🔥 UPDATE EACH ITEM ALSO
        for (OrderItem item : order.getItems()) {
            item.setItemStatus(status);
        }

        order.setUpdatedAt(System.currentTimeMillis());

        return orderRepository.save(order);
    }


    @Override
    public Order adminUpdatePaymentStatus(Long orderId, String newPaymentStatus) {
        if (newPaymentStatus == null || newPaymentStatus.isBlank()) {
            throw new RuntimeException("Payment status cannot be empty");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(newPaymentStatus.trim().toUpperCase());
        return orderRepository.save(order);
    }

    // ================= DASHBOARD =================

    @Override
    public AdminDashboardDto getOrderDashboard() {
        List<Order> orders = orderRepository.findAll();
        List<User> users = userRepository.findAll();

        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalUsers(users.size());
        dto.setTotalSellers(
                users.stream().filter(u -> "SELLER".equalsIgnoreCase(u.getRole())).count()
        );
        dto.setPendingSellers(
                users.stream()
                        .filter(u -> "SELLER".equalsIgnoreCase(u.getRole()) && !u.isSellerVerified())
                        .count()
        );

        dto.setTotalOrders(orders.size());

        long pending = 0, shipped = 0, delivered = 0, cancelled = 0;
        double revenue = 0, codPending = 0, onlinePaid = 0;

        for (Order o : orders) {
            String status = o.getOrderStatus() == null ? "" : o.getOrderStatus().toUpperCase();
            String payStatus = o.getPaymentStatus() == null ? "" : o.getPaymentStatus().toUpperCase();
            String method = o.getPaymentMethod() == null ? "" : o.getPaymentMethod().toUpperCase();

            double amount = o.getTotalAmount() == null ? 0 : o.getTotalAmount();

            switch (status) {
                case "PENDING" -> pending++;
                case "SHIPPED" -> shipped++;
                case "DELIVERED" -> delivered++;
                case "CANCELLED" -> cancelled++;
            }

            if ("PAID".equals(payStatus)) revenue += amount;
            if ("COD".equals(method) && "PENDING".equals(payStatus)) codPending += amount;
            if (!"COD".equals(method) && "PAID".equals(payStatus)) onlinePaid += amount;
        }

        dto.setPendingOrders(pending);
        dto.setShippedOrders(shipped);
        dto.setDeliveredOrders(delivered);
        dto.setCancelledOrders(cancelled);

        dto.setTotalRevenue(revenue);
        dto.setCodPendingAmount(codPending);
        dto.setOnlinePaidAmount(onlinePaid);

        return dto;
    }
}
