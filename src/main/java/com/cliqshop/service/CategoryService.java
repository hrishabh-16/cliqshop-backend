package com.cliqshop.service;

import com.cliqshop.dto.CategoryDto;
import com.cliqshop.entity.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category getCategoryById(Long id);
    Category createCategory(CategoryDto categoryDto);
    Category updateCategory(Long id, CategoryDto categoryDto);
    void deleteCategory(Long id);
}