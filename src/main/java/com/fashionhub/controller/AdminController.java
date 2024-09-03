package com.fashionhub.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fashionhub.model.Category;
import com.fashionhub.model.Product;
import com.fashionhub.model.UserDtls;
import com.fashionhub.service.CategoryService;
import com.fashionhub.service.ProductService;
import com.fashionhub.service.UserService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;
    
	@ModelAttribute
	public void getUserDetails(Principal p,Model m){
		if (p != null) {
			String email = p.getName();
			UserDtls user = userService.getUserByEmail(email);
			m.addAttribute("user", user);
            
		}
	}

    
    @GetMapping("/")
    public String index(){
        return "admin/index";
    }

    
    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m){
        List<Category> categories = categoryService.getAllCategory();
        m.addAttribute("categories", categories);
        return "admin/addproduct";
    }
    
    @GetMapping("/category")
    public String category(Model model){
        model.addAttribute("categorys", categoryService.getAllCategory());
        return "admin/category"; 
    }

    @GetMapping("/addAdmin")
    public String addAdmin(){
        return "admin/add_admin";
    }
  

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException {
      
        String imagename = file.isEmpty()?category.getImageName():file.getOriginalFilename();
        category.setImageName(imagename);
        if (!categoryService.exist(category.getName())) {
        Category saveCat = categoryService.saveCategory(category);
        if (ObjectUtils.isEmpty(saveCat)) {
            session.setAttribute("ErrorMsg", "Not Saved ! Internal Server Error");
        }else{
            File saveFile = new ClassPathResource("static/img/").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());
            System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("SuccMsg", "Saved Successfully");
        }
        }else{
            session.setAttribute("ErrorMsg", "Category Already Exists");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id ,HttpSession session){
        Boolean b = categoryService.deleteCategory(id);
        if(b){
            session.setAttribute("SuccMsg", "Category Deleted Succesfully");
        }else{
            session.setAttribute("ErrorMsg", "Some thing went wrong");
        }
        return "redirect:/admin/category";
    }

   

    @PostMapping("/saveAdmin")
	public String saveUser(@ModelAttribute UserDtls user,@RequestParam("img") MultipartFile file,HttpSession session) throws IOException{
		String imageName = file.isEmpty() ? "/img/4.jpg":file.getOriginalFilename();
		 user.setProfileImage(imageName);
         user.setIsEnable(true);
         user.setRole("ROLE_ADMIN");
		 UserDtls saveUser =userService.saveUser(user);
		if (!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
			// File saveFile = new ClassPathResource("static/img").getFile();

           Path path = Paths.get("src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"img"+File.separator+"profile_img"+File.separator+File.separator+file.getOriginalFilename());System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("SuccMsg", "Saved Successfully");
			}
		}else{
			session.setAttribute("ErrorMsg", "Category Already Exists");
		}
		return "redirect:/register";
	}


   

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id,Model m){
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }


    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category,@RequestParam("file") MultipartFile file,HttpSession session) throws IOException{
       
        Category cat = categoryService.getCategoryById(category.getId());
        String fileName = file.isEmpty()?cat.getImageName():file.getOriginalFilename();
        if (!ObjectUtils.isEmpty(category)) {
            
            cat.setName(category.getName());
            cat.setIsActive(category.getIsActive());
            cat.setImageName(fileName);
        }
       Category updateCategory = categoryService.saveCategory(cat);
       if (!ObjectUtils.isEmpty(updateCategory)) {
        if (!file.isEmpty()) {
            // File saveFile = new ClassPathResource("static/img/").getFile();

            Path path = Paths.get("src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"img"+File.separator+"category_img"+File.separator+file.getOriginalFilename());
            System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);

        }
        session.setAttribute("SuccMsg", "Update SucessFul ");
       }  
       else{
        session.setAttribute("ErrorMsg", "There Is Some Issue");
       } 
       return "redirect:/admin/category" ;
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile file ,HttpSession session) throws IOException{
        String fileName = file.isEmpty()?"4.jpg":file.getOriginalFilename();
        product.setImageName(fileName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice());
        Product saveProduct = productService.saveProduct(product);
        if (!ObjectUtils.isEmpty(saveProduct)) {

           
            Path path = Paths.get("src"+File.separator+"main"+File.separator+"resources"+File.separator+"static"+File.separator+"img"+File.separator+"product_img"+File.separator+File.separator+file.getOriginalFilename());
            System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("SuccMsg", "Product Saved SucessFully");
        }else{
            session.setAttribute("ErrorMsg", "Something went wrong");
        }
        return "redirect:/admin/loadAddProduct";
    }


    @GetMapping("/viewProduct")
    public String loadViewProduct(Model m){
        m.addAttribute("products", productService.getAllProducts());
        return "/admin/view_all_product";
    }

    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id ,HttpSession session){
        Boolean b = productService.deleteProductByid(id);
        if(b){
            session.setAttribute("SuccMsg", "Category Deleted Succesfully");
        }else{
            session.setAttribute("ErrorMsg", "Some thing went wrong");
        }
        return "redirect:/admin/viewProduct";
    }


    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id,Model m){
        m.addAttribute("categories", categoryService.getAllCategory());
        m.addAttribute("product", productService.getProductById(id));
        return "/admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile file,HttpSession session ) throws IOException{
        Product pro = productService.getProductById(product.getId());
        String fileName = file.isEmpty()?"4.jpg":file.getOriginalFilename();
            pro.setCategory(product.getCategory());
            pro.setDescription(product.getDescription());
            pro.setStock(product.getStock());
            pro.setTitle(product.getTitle());
            pro.setIsActive(product.getIsActive());
            pro.setDiscount(product.getDiscount());
            pro.setDiscountPrice((int)(product.getPrice()*(product.getDiscount() / 100.0)));
            pro.setPrice(product.getPrice());
            pro.setImageName(fileName);
           int discountPrice = product.getPrice()-(product.getPrice()/100)*product.getDiscount();
           pro.setDiscountPrice(discountPrice);
       Product updateCategory = productService.saveProduct(pro);
       if (!ObjectUtils.isEmpty(updateCategory)) {
       try{
            File saveFile = new ClassPathResource("static/img/").getFile();

            Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"product_img"+File.separator+file.getOriginalFilename());
            System.out.print(path);
            Files.copy(file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        session.setAttribute("SuccMsg", "Update SucessFul ");
       }  
       else{
        session.setAttribute("ErrorMsg", "There Is Some Issue");
       } 
       return "redirect:/admin/loadEditProduct/"+product.getId();
    }

    @GetMapping("/users")
    public String getAllUserss(Model m){
        m.addAttribute("users", userService.getUserByRole("ROLE_USER"));
        return "/admin/view_users";
    }

    @GetMapping("/updateStatus")
   public String updateUserAccount(@RequestParam Boolean status,@RequestParam Integer id){
        userService.updateAccount(id,status);
        return "redirect:/admin/users";
   }
}
