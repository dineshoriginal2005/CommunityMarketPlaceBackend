package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.LoginDto;
import com.CommunityMarketPlace.New.dto.LoginResponse;
import com.CommunityMarketPlace.New.dto.RegisterDto;
import com.CommunityMarketPlace.New.model.User;

public interface AuthService {
    User register(RegisterDto dto);
    LoginResponse login(LoginDto dto);
    User registerSeller(RegisterDto dto);
    User registerAdmin(RegisterDto dto);
    boolean emailExists(String email);


}
