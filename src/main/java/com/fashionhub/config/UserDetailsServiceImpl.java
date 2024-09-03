package com.fashionhub.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fashionhub.model.UserDtls;
import com.fashionhub.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        UserDtls user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUser(user);
    }
    
}
