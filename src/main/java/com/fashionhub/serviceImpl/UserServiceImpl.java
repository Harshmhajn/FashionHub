package com.fashionhub.serviceImpl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fashionhub.Utils.AppConstants;
import com.fashionhub.model.UserDtls;
import com.fashionhub.repository.UserRepository;
import com.fashionhub.service.UserService;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public UserDtls saveUser(UserDtls user) {
        String encodePassword = passwordEncoder.encode(user.getPassword());
        
        user.setAccountNonLocked(true);
        user.setFailedAttempt(0);
        user.setPassword(encodePassword);
        UserDtls saveUser = userRepository.save(user);
        return saveUser;
    }
    @Override
    @Cacheable(value = "userCache", key = "#email")
    public UserDtls getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public List<UserDtls> getUserByRole(String role) {
        return userRepository.findByRole(role);
    }
    @Override
    public UserDtls getUserById(int id) {
        return userRepository.findById(id).get();
    }
    @Async
    @Override
    public void increaseFailedAttempt(UserDtls user) {
       int attemp =user.getFailedAttempt()+1;
       user.setFailedAttempt(attemp);
       userRepository.save(user);
    }
    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }
    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime+AppConstants.UNLOCK_DURATION;

        long currentTime = System.currentTimeMillis();
        if(unlockTime < currentTime)
        {
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            user.setLockTime(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    public void resetAttept(int userId) {

    }

    @Override
    public Boolean updateAccount(Integer id, Boolean status) {

        Optional<UserDtls> findByUser = userRepository.findById(id);
        if (findByUser.isPresent()) {
            UserDtls user = findByUser.get();
            user.setIsEnable(status);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    @Override
    public void updateUserResetToken(String email, String resetToken) {
       UserDtls user = userRepository.findByEmail(email);
       user.setResetToken(resetToken);
       userRepository.save(user);
    }
    @Override
    public UserDtls getUserByToken(String token) {
        return userRepository.findByResetToken(token);
    }
    @Override
    public UserDtls updateUser(UserDtls user) {
       return userRepository.save(user);
    }
    
}
