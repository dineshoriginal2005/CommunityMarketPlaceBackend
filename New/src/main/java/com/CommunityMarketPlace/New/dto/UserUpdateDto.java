package com.CommunityMarketPlace.New.dto;

import lombok.Data;

@Data
public class UserUpdateDto {

    private String phoneNumber;
    private String fullName;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;
}
