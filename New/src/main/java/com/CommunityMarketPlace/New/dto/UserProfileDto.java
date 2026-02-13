package com.CommunityMarketPlace.New.dto;

import com.CommunityMarketPlace.New.model.User;
import lombok.Data;

@Data
public class UserProfileDto {

    private Long id;
    private String name;
    private String email;
    private String role;

    private String phoneNumber;
    private String fullName;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String pincode;
    private String country;

    public UserProfileDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.phoneNumber = user.getPhoneNumber();
        this.fullName = user.getFullName();
        this.addressLine1 = user.getAddressLine1();
        this.addressLine2 = user.getAddressLine2();
        this.city = user.getCity();
        this.state = user.getState();
        this.pincode = user.getPincode();
        this.country = user.getCountry();
    }
}
