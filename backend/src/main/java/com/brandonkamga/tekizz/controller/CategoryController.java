package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.service.interfaces.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Category operations.
 * Provides endpoints for fetching categories for QCM games.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Get all active categories.
     * 
     * GET /api/categories
     * 
     * Returns list of active categories ordered by display order.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAllCategories() {
        List<Category> categories = categoryService.findAllActiveOrderByDisplayOrder();
        
        List<CategoryResponse> response = categories.stream()
                .map(this::toCategoryResponse)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Categories retrieved successfully"));
    }

    /**
     * Get a category by ID.
     * 
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        return ResponseEntity.ok(ApiResponse.success(toCategoryResponse(category), "Category retrieved successfully"));
    }

    private CategoryResponse toCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getDisplayOrder()
        );
    }

    /**
     * DTO for Category response.
     */
    public record CategoryResponse(
            Long id,
            String name,
            String slug,
            String description,
            Integer displayOrder
    ) {}
}
