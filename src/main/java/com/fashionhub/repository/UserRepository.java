package com.fashionhub.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;

import com.fashionhub.model.UserDtls;

public interface UserRepository extends JpaRepository<UserDtls,Integer> {
    public UserDtls findByEmail(String email);
    public List<UserDtls> findByRole(String role);
    public UserDtls findByResetToken(String token);
}
