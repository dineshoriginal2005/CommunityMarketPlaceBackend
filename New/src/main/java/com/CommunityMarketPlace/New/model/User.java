package com.CommunityMarketPlace.New.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Information
    private String name;
    private String email;
    private String password;

    // USER / SELLER / ADMIN
    private String role;

    // Seller verification by Admin
    private boolean sellerVerified = false;

    // Delivery Information (optional)
    private String fullName;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    private boolean blocked = false;

// generate getter/setter or let Lombok handle it (@Data)

}
