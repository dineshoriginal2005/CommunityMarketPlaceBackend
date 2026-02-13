package com.CommunityMarketPlace.New.repository;

import com.CommunityMarketPlace.New.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
