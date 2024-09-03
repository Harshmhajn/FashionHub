package com.fashionhub.controller;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fashionhub.Utils.CommonUtil;
import com.fashionhub.model.Category;
import com.fashionhub.model.Product;
import com.fashionhub.model.UserDtls;
import com.fashionhub.service.CategoryService;
import com.fashionhub.service.ProductService;
import com.fashionhub.service.UserService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	@Autowired
	private ProductService productService;

	@Autowired
	private CommonUtil commonUtil;
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private UserService userService;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@ModelAttribute
	public void getUserDetails(Principal p,Model m){
		if (p != null) {
			String email = p.getName();
			UserDtls user = userService.getUserByEmail(email);
			m.addAttribute("user", user);
		}
	}
	
	@GetMapping("/")
	public String index(Principal p,Model m) {
		if (p != null) {
			String email = p.getName();
			UserDtls user = userService.getUserByEmail(email);
			m.addAttribute("user", user);
		}
		return "index";
	}

	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/about")
	public String about(){
		return "about";
	}

	@GetMapping("/product")
	public String product(Model m) {
		List<Product> products = productService.getAllActiveProducts();
		List<Category> categories = categoryService.getAllActiveCategory();
		m.addAttribute("products", products);
		m.addAttribute("categories", categories);
		return "product";
	}

	@GetMapping("/view_product/{id}")
	public String view_detail(@PathVariable int id,Model m) {
		Product product = productService.getProductById(id);
		m.addAttribute("product", product);
		return "view_product";
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDtls user,@RequestParam("img") MultipartFile file,HttpSession session) throws IOException{
		String imageName = file.isEmpty() ? "/img/4.jpg":file.getOriginalFilename();
		 user.setProfileImage(imageName);
		 
         user.setIsEnable(true);
		 user.setRole("ROLE_USER");
		 UserDtls saveUser =userService.saveUser(user);
		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
			// File saveFile = new ClassPathResource("static/img").getFile();

           Path path = Paths.get("src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"img"+File.separator+"profile_img"+File.separator+File.separator+file.getOriginalFilename());System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("SuccMsg", "Saved Successfully");
			}
		}else{
			session.setAttribute("ErrorMsg", "User Already Exists");
		}
		return "redirect:/register";
	}

	// Forgot Password

	@GetMapping("/forgot-password")
	public String showForgotPassword(){
		return "forgot_password";
	}

	@PostMapping("/forgot-password")
	public String processForgotPassword(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException{
		UserDtls user = userService.getUserByEmail(email);
		if (ObjectUtils.isEmpty(user)) {
			session.setAttribute("SuccMsg", "invalid email");
		}else{
			String resetToken =  UUID.randomUUID().toString();
			userService.updateUserResetToken(email,resetToken);
			
			// generate URL
			String url = CommonUtil.geneateURL(request)+"/reset-password?token="+resetToken;
			
			Boolean b = commonUtil.sendMail(url,email);
			if (b) {
				session.setAttribute("SuccMsg", "Please  Check your mail");
			}else{
				session.setAttribute("msg", "Something went wrong");
				return "error";
			}
		}
		return "redirect:/forgot-password";
	}


	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token,Model m,HttpSession session){
		UserDtls user = userService.getUserByToken(token);
		System.out.print(user == null);
		if (ObjectUtils.isEmpty(user)) {
			session.setAttribute("msg", "Your Link is Invalid Or Expired!!");
			return "error";
		}
		m.addAttribute("token", token);
		return "reset_password";
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token,@RequestParam String password,HttpSession session){
		UserDtls user = userService.getUserByToken(token);
		if (ObjectUtils.isEmpty(user)) {
			session.setAttribute("ErrorMsg", "Token Is Wrong");
			return "error";
		}else{
			user.setPassword(passwordEncoder.encode(password));
			user.setResetToken(null);
			userService.updateUser(user);
			session.setAttribute("SuccMsg", "Password Changed Sucessfully");
		}
		return "redirect:/signin";
	}

}
