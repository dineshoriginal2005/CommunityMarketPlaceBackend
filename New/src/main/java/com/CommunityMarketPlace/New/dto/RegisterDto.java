package com.CommunityMarketPlace.New.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String name;
    private String email;
    private String password;

    // seller fields
    private String businessName;
    private String gstNumber;
    private String shopAddress;
    private String businessPhone;  // FIXED
}
