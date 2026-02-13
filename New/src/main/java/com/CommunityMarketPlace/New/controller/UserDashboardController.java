package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.UserDashboardDto;
import com.CommunityMarketPlace.New.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/dashboard")
public class UserDashboardController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<UserDashboardDto> getDashboard() {
        return ResponseEntity.ok(userService.getUserDashboard());
    }
}
