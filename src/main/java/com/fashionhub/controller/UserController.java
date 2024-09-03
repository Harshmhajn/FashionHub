package com.fashionhub.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fashionhub.model.UserDtls;
import com.fashionhub.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String home(){
        return "user/home";
    }
    
	@ModelAttribute
	public void getUserDetails(Principal p,Model m){
		if (p != null) {
			String email = p.getName();
			UserDtls user = userService.getUserByEmail(email);
			m.addAttribute("user", user);
		}
	}

	@GetMapping("/cart")
	public String cart(){
		return "user/cart";
	}
}
