package com.CommunityMarketPlace.New.service;

import com.CommunityMarketPlace.New.dto.UserDashboardDto;
import com.CommunityMarketPlace.New.dto.UserProfileDto;
import com.CommunityMarketPlace.New.dto.UserUpdateDto;
import com.CommunityMarketPlace.New.dto.ChangePasswordDto;

public interface UserService {

    UserProfileDto getProfile(Long userId);

    UserProfileDto updateProfile(Long userId, UserUpdateDto dto);
    UserDashboardDto getUserDashboard();

    void changePassword(Long userId, ChangePasswordDto dto);
}
