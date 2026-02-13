package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.*;
import com.CommunityMarketPlace.New.model.Order;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.repository.OrderRepository;
import com.CommunityMarketPlace.New.repository.UserRepository;
import com.CommunityMarketPlace.New.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ========================
    // GET CURRENT LOGGED-IN USER
    // ========================
    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        return (User) principal;
    }

    // DTO mapper
    private UserProfileDto mapToDto(User user) {
        return new UserProfileDto(user);
    }

    // ========================
    // GET PROFILE
    // ========================
    @Override
    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDto(user);
    }

    // ========================
    // UPDATE PROFILE
    // ========================
    @Override
    public UserProfileDto updateProfile(Long userId, UserUpdateDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (dto.getPhoneNumber() != null) user.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        if (dto.getAddressLine1() != null) user.setAddressLine1(dto.getAddressLine1());
        if (dto.getAddressLine2() != null) user.setAddressLine2(dto.getAddressLine2());
        if (dto.getCity() != null) user.setCity(dto.getCity());
        if (dto.getState() != null) user.setState(dto.getState());
        if (dto.getPincode() != null) user.setPincode(dto.getPincode());
        if (dto.getCountry() != null) user.setCountry(dto.getCountry());

        return mapToDto(userRepository.save(user));
    }

    // ========================
    // USER DASHBOARD
    // ========================
    @Override
    public UserDashboardDto getUserDashboard() {

        User user = getCurrentUser();

        List<Order> orders = orderRepository.findByUser(user);
        UserDashboardDto dto = new UserDashboardDto();

        // profile
        dto.setUserId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setFullName(user.getFullName());
        dto.setAddressLine1(user.getAddressLine1());
        dto.setAddressLine2(user.getAddressLine2());
        dto.setCity(user.getCity());
        dto.setState(user.getState());
        dto.setPincode(user.getPincode());
        dto.setCountry(user.getCountry());

        // order summary
        dto.setTotalOrders(orders.size());
        dto.setPendingOrders(orders.stream().filter(o -> "PENDING".equals(o.getOrderStatus())).count());
        dto.setShippedOrders(orders.stream().filter(o -> "SHIPPED".equals(o.getOrderStatus())).count());
        dto.setDeliveredOrders(orders.stream().filter(o -> "DELIVERED".equals(o.getOrderStatus())).count());
        dto.setCancelledOrders(orders.stream().filter(o -> "CANCELLED".equals(o.getOrderStatus())).count());

        // payment summary
        dto.setTotalSpent(
                orders.stream()
                        .filter(o ->
                                ("PAID".equalsIgnoreCase(o.getPaymentStatus())) ||
                                        ("COD".equalsIgnoreCase(o.getPaymentMethod()) &&
                                                "DELIVERED".equalsIgnoreCase(o.getOrderStatus()))
                        )
                        .mapToDouble(Order::getTotalAmount)
                        .sum()
        );


        dto.setOnlinePaidAmount(
                orders.stream()
                        .filter(o -> !"COD".equalsIgnoreCase(o.getPaymentMethod()))
                        .filter(o -> "PAID".equalsIgnoreCase(o.getPaymentStatus()))
                        .mapToDouble(Order::getTotalAmount)
                        .sum()
        );

        dto.setCodAmount(
                orders.stream()
                        .filter(o -> "COD".equalsIgnoreCase(o.getPaymentMethod()))
                        .filter(o -> "DELIVERED".equalsIgnoreCase(o.getOrderStatus()))
                        .mapToDouble(Order::getTotalAmount)
                        .sum()
        );

        // last 5 orders
        dto.setLastOrders(
                orders.stream()
                        .sorted((a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()))
                        .limit(5)
                        .map(order -> {
                            UserLastOrderDto lo = new UserLastOrderDto();
                            lo.setOrderId(order.getId());
                            lo.setOrderStatus(order.getOrderStatus());
                            lo.setPaymentStatus(order.getPaymentStatus());
                            lo.setTotalAmount(order.getTotalAmount());
                            lo.setCreatedAt(order.getCreatedAt());
                            return lo;
                        })
                        .toList()
        );

        return dto;
    }

    // ========================
    // CHANGE PASSWORD
    // ========================
    @Override
    public void changePassword(Long userId, ChangePasswordDto dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
