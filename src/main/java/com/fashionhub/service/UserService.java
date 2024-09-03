package com.fashionhub.service;

import java.util.List;

import com.fashionhub.model.UserDtls;

public interface UserService {
    public UserDtls saveUser(UserDtls user);
    public UserDtls getUserByEmail(String email);
    public List<UserDtls> getUserByRole(String role);
    public UserDtls getUserById(int id);
    public void increaseFailedAttempt(UserDtls user);
    public void userAccountLock(UserDtls user);
    public boolean unlockAccountTimeExpired(UserDtls user);
    public void resetAttept(int id);
    public Boolean updateAccount(Integer id, Boolean status);
    public void updateUserResetToken(String email,String resetToken);
    public UserDtls getUserByToken(String token);
    public UserDtls updateUser(UserDtls user);
}
