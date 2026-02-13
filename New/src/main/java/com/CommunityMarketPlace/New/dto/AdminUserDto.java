package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class AdminUserDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean sellerVerified;
    private boolean blocked;
}
