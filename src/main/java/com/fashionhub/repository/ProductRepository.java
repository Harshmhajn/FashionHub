package com.fashionhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionhub.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer>{

    List<Product> findByIsActiveTrue();
}
