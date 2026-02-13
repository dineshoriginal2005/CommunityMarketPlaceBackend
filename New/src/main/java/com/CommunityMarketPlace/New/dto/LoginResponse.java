package com.CommunityMarketPlace.New.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String message;
    private String role;
    private boolean sellerVerified;
    private Long userId;
    private String token;
    private String name;        //Newly added after upload the Github for my understanding
    private String email;       //Newly added after upload the Github for my understanding
}
