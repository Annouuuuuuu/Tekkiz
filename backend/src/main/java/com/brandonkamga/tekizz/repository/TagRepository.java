package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.Tag;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    Optional<Tag> findBySlug(String slug);
    
    List<Tag> findBySlugIn(List<String> slugs);
    
    List<Tag> findByIsActiveTrue();
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
    
    List<Tag> findByNameIn(List<String> names);
    
    /**
     * Find all active tags belonging to a specific category.
     * Used for specialty tags like React (Frontend), SpringBoot (Backend), etc.
     */
    List<Tag> findByCategoryIdAndIsActiveTrue(Long categoryId);
    
    /**
     * Find all tags belonging to a specific category.
     */
    List<Tag> findByCategoryId(Long categoryId);
}
