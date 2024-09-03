package com.fashionhub.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fashionhub.model.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer>{
    
    public Boolean existsByName(String name);

    public List<Category> findByIsActiveTrue();
}
