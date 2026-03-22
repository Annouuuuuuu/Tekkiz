package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.Tag;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.repository.TagRepository;
import com.brandonkamga.tekizz.service.interfaces.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Tag operations.
 * Provides endpoints for fetching tags for QCM games.
 * Tags are now directly linked to categories (specialty tags).
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;
    private final TagRepository tagRepository;

    public TagController(TagService tagService, TagRepository tagRepository) {
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    /**
     * Get all active tags.
     * 
     * GET /api/tags
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {
        List<Tag> tags = tagService.findAllActive();
        
        List<TagResponse> response = tags.stream()
                .map(this::toTagResponse)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Tags retrieved successfully"));
    }

    /**
     * Get tags by category ID.
     * 
     * GET /api/tags/category/{categoryId}
     * 
     * Returns all specialty tags directly linked to the given category.
     * Example: Frontend category -> React, Vue, Angular, etc.
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getTagsByCategory(@PathVariable Long categoryId) {
        // Get tags directly linked to the category (specialty tags)
        List<Tag> tags = tagRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        
        List<TagResponse> response = tags.stream()
                .map(this::toTagResponse)
                .toList();
        
        return ResponseEntity.ok(ApiResponse.success(response, "Tags retrieved successfully"));
    }

    /**
     * Get a tag by ID.
     * 
     * GET /api/tags/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponse>> getTagById(@PathVariable Long id) {
        Tag tag = tagService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
        
        return ResponseEntity.ok(ApiResponse.success(toTagResponse(tag), "Tag retrieved successfully"));
    }

    private TagResponse toTagResponse(Tag tag) {
        return new TagResponse(
                tag.getId(),
                tag.getName(),
                tag.getSlug(),
                tag.getDescription(),
                tag.getCategory() != null ? tag.getCategory().getId() : null,
                tag.getCategory() != null ? tag.getCategory().getName() : null
        );
    }

    /**
     * DTO for Tag response.
     */
    public record TagResponse(
            Long id,
            String name,
            String slug,
            String description,
            Long categoryId,
            String categoryName
    ) {}
}
