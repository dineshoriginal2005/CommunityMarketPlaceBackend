package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.ChangePasswordDto;
import com.CommunityMarketPlace.New.dto.UserProfileDto;
import com.CommunityMarketPlace.New.dto.UserUpdateDto;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // ✔ Extract userId safely
    private Long extractUserId(Authentication auth) {
        Object principal = auth.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authentication principal");
        }

        return ((User) principal).getId();
    }

    // ===========================
    // GET PROFILE
    // ===========================
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    public ResponseEntity<UserProfileDto> getProfile(Authentication auth) {
        Long userId = extractUserId(auth);
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    // ===========================
    // UPDATE PROFILE
    // ===========================
    @PutMapping("/update-profile")
    @PreAuthorize("hasAnyRole('USER', 'SELLER')")
    public ResponseEntity<UserProfileDto> updateProfile(
            Authentication auth,
            @RequestBody UserUpdateDto dto) {

        Long userId = extractUserId(auth);
        return ResponseEntity.ok(userService.updateProfile(userId, dto));
    }

    // ===========================
    // CHANGE PASSWORD
    // ===========================
    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('USER', 'SELLER', 'ADMIN')")
    public ResponseEntity<String> changePassword(
            Authentication auth,
            @RequestBody ChangePasswordDto dto) {

        Long userId = extractUserId(auth);
        userService.changePassword(userId, dto);

        return ResponseEntity.ok("Password updated successfully");
    }
}
