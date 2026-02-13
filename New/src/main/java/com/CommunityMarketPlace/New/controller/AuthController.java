package com.CommunityMarketPlace.New.controller;

import com.CommunityMarketPlace.New.dto.LoginDto;
import com.CommunityMarketPlace.New.dto.RegisterDto;
import com.CommunityMarketPlace.New.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:3000") // if frontend is on different port
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register-admin")
    public ResponseEntity<?> registerAdmin(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(authService.registerAdmin(dto));
    }

    @PostMapping("/register-seller")
    public ResponseEntity<?> registerSeller(@RequestBody RegisterDto dto) {
        return ResponseEntity.ok(authService.registerSeller(dto));
    }

    // ✅ NEW — EMAIL EXISTENCE CHECK (VERY IMPORTANT)
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
