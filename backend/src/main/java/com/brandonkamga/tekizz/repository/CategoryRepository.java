package com.brandonkamga.tekizz.repository;

import com.brandonkamga.tekizz.domain.Category;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    Optional<Category> findByName(String name);
    
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByIsActiveTrue();
    
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
}
