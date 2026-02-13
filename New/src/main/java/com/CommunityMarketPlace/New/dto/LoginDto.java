package com.CommunityMarketPlace.New.dto;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
        private String email;
        private String password;
    }
