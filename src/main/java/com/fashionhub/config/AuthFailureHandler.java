package com.fashionhub.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fashionhub.Utils.AppConstants;
import com.fashionhub.model.UserDtls;
import com.fashionhub.repository.UserRepository;
import com.fashionhub.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandler extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
                String email = request.getParameter("username");
                UserDtls userDtls = userRepository.findByEmail(email);
                if(userDtls.getIsEnable()){
                    if(userDtls.getAccountNonLocked()){
                        if (userDtls.getFailedAttempt() <= AppConstants.NUMBER_OF_ATTEMPT) {
                            userService.increaseFailedAttempt(userDtls);
                        }else{
                            userService.userAccountLock(userDtls);
                            exception = new LockedException("Your Account is Locked");
                        }
                    }else{
                        if (userService.unlockAccountTimeExpired(userDtls)) {
                            exception = new LockedException("Your Account is Unlocked");
                        }
                    exception = new LockedException("Your Account Locked");
                    }
                }else{
                    exception = new LockedException("Your Account Blocked For A while");
                }

                super.setDefaultFailureUrl("/signin?error");
                super.onAuthenticationFailure(request, response, exception);
    }
    
}
