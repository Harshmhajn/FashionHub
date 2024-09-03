package com.fashionhub.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fashionhub.model.Product;
import com.fashionhub.repository.ProductRepository;
import com.fashionhub.service.ProductService;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Product saveProduct(Product product) {
        Product p = productRepository.save(product);
        return p;    
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Boolean deleteProductByid(int id) {
       Product product =  productRepository.findById(id).orElse(null);
      if (product != null) {
        productRepository.delete(product);
        return true;
      }
      return false;
    }

    @Override
    public Product getProductById(int id) {
      Product p =  productRepository.findById(id).orElse(null);
      return p;
    }

    @Override
    public List<Product> getAllActiveProducts() {
      return productRepository.findByIsActiveTrue();
    }
}
