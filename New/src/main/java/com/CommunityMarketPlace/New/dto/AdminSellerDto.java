package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class AdminSellerDto {

    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean sellerVerified;
    private boolean isBlocked;

    // SELLER PROFILE FIELDS
    private String businessName;
    private String gstNumber;
    private String shopAddress;
    private String businessPhone;
    private String status;
}
