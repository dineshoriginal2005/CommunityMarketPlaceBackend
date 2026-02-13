package com.CommunityMarketPlace.New.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sellers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seller {

    @Id
    private Long id;  // Same as User ID

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;

    private String businessName;
    private String gstNumber;
    private String shopAddress;
    private String businessPhone;  // FIXED

    private String status = "PENDING"; // Admin approves later
}
