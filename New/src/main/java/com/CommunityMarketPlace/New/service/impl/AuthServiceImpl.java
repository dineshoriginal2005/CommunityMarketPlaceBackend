package com.CommunityMarketPlace.New.service.impl;

import com.CommunityMarketPlace.New.dto.LoginDto;
import com.CommunityMarketPlace.New.dto.LoginResponse;
import com.CommunityMarketPlace.New.dto.RegisterDto;
import com.CommunityMarketPlace.New.model.Seller;
import com.CommunityMarketPlace.New.model.User;
import com.CommunityMarketPlace.New.repository.SellerRepository;
import com.CommunityMarketPlace.New.repository.UserRepository;
import com.CommunityMarketPlace.New.security.JwtUtil;
import com.CommunityMarketPlace.New.service.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ---------------------------------------------------------
    // REGISTER NORMAL USER
    // ---------------------------------------------------------
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User register(RegisterDto dto) {

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        return userRepository.save(user);
    }

    // ---------------------------------------------------------
    // LOGIN (ALL ROLES: USER / SELLER / ADMIN)
    // ---------------------------------------------------------
    @Override
    public LoginResponse login(LoginDto dto) {

        User user = userRepository.findByEmail(dto.getEmail());

        if (user == null) {
            throw new RuntimeException("User does not exist");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole(), user.getId());

        return new LoginResponse(
                "Login successful",
                user.getRole(),
                user.isSellerVerified(),
                user.getId(),
                token,
                user.getName(),      // ADD THIS LINE
                user.getEmail()      // ADD THIS LINE
        );
    }

    // ---------------------------------------------------------
    // REGISTER ADMIN
    // ---------------------------------------------------------
    @Override
    public User registerAdmin(RegisterDto dto) {

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        User admin = new User();
        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        admin.setRole("ADMIN");

        return userRepository.save(admin);
    }

    // ---------------------------------------------------------
    // REGISTER SELLER: User + Seller Profile
    // ---------------------------------------------------------
    @Override
    public User registerSeller(RegisterDto dto) {

        if (userRepository.findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Email already registered");
        }

        // Create USER entry
        User seller = new User();
        seller.setName(dto.getName());
        seller.setEmail(dto.getEmail());
        seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        seller.setRole("SELLER");
        seller.setSellerVerified(false);

        User savedUser = userRepository.save(seller);

        // Create SELLER PROFILE entry
        Seller sellerProfile = new Seller();
        sellerProfile.setUser(savedUser);                 // @MapsId → maps ID
        sellerProfile.setBusinessName(dto.getBusinessName());
        sellerProfile.setGstNumber(dto.getGstNumber());
        sellerProfile.setShopAddress(dto.getShopAddress());
        sellerProfile.setBusinessPhone(dto.getBusinessPhone());   // FIXED
        sellerProfile.setStatus("PENDING");

        sellerRepository.save(sellerProfile);

        return savedUser;
    }
}
