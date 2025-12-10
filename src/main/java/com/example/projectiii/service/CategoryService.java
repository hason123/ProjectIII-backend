package com.example.projectiii.service;

import com.example.projectiii.dto.request.CategoryRequest;
import com.example.projectiii.dto.response.CategoryResponse;
import com.example.projectiii.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface CategoryService {

    CategoryResponse getCategory(Long id);

    CategoryResponse addCategory(CategoryRequest request);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    PageResponse<CategoryResponse> getAllCategories(Pageable pageable);

    void createCategoryWorkbook(HttpServletResponse response) throws IOException;
}
