package com.example.projectiii.controller;

import com.example.projectiii.dto.request.CategoryRequest;
import com.example.projectiii.dto.response.CategoryResponse;
import com.example.projectiii.dto.response.PageResponse;
import com.example.projectiii.service.CategoryService;
//import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/library/")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController( CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories")
    public ResponseEntity<PageResponse<CategoryResponse>> getCategories(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                                              @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        PageResponse<CategoryResponse> categoryPage = categoryService.getAllCategories(pageable);
        return ResponseEntity.ok(categoryPage);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable long id) {
        CategoryResponse category = categoryService.getCategory(id);
        return ResponseEntity.ok(category);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PostMapping("/categories")
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest category) {
        CategoryResponse categoryAdded = categoryService.addCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryAdded);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable long id, @RequestBody CategoryRequest category) {
        CategoryResponse categoryUpdated = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryUpdated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
        Map<String, String> response = Map.of("message", "Delete successful");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    @GetMapping("/categories/dashboard")
    public ResponseEntity<Void> getCategoryDashboard(HttpServletResponse response) throws IOException {
        categoryService.createCategoryWorkbook(response);
        return ResponseEntity.ok().build();
    }
}
