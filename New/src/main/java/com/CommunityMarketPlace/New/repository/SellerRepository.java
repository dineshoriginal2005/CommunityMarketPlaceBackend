package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    // Check if seller profile already exists (userId == sellerId)
    boolean existsById(Long userId);

    // Get seller by user ID (same as seller ID)
    Optional<Seller> findByUserId(Long userId);

    // Admin: get all sellers by status (PENDING / APPROVED / REJECTED)
    List<Seller> findByStatus(String status);
}
