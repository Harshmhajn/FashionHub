package com.fashionhub.service;

import java.util.List;

import com.fashionhub.model.Product;

public interface ProductService {
    public Product saveProduct(Product product);
    public List<Product> getAllProducts();
    public Boolean deleteProductByid(int id);
    public Product getProductById(int id);
    public List<Product> getAllActiveProducts();
}
