package com.CommunityMarketPlace.New.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Parent order
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // Snapshot fields
    private Long productId;
    private String productName;
    private String productImage;

    private Long sellerId;

    private Integer quantity;
    private Double priceAtPurchase;
    private Double totalPrice;

    private String itemStatus;
}
