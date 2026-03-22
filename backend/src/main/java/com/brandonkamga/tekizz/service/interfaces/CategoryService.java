package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Category;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Category operations.
 * Follows Interface Segregation Principle.
 */
public interface CategoryService {

    /**
     * Find a category by its ID.
     *
     * @param id the category ID
     * @return the category if found
     */
    Optional<Category> findById(Long id);

    /**
     * Find a category by its name.
     *
     * @param name the category name
     * @return the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Find a category by its slug.
     *
     * @param slug the category slug
     * @return the category if found
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Find all active categories.
     *
     * @return list of active categories
     */
    List<Category> findAllActive();

    /**
     * Find all active categories ordered by display order.
     *
     * @return list of active categories ordered by display order
     */
    List<Category> findAllActiveOrderByDisplayOrder();

    /**
     * Find all categories.
     *
     * @return list of all categories
     */
    List<Category> findAll();

    /**
     * Save a category.
     *
     * @param category the category to save
     * @return the saved category
     */
    Category save(Category category);

    /**
     * Delete a category by its ID.
     *
     * @param id the category ID
     */
    void deleteById(Long id);

    /**
     * Check if a category exists by name.
     *
     * @param name the category name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a category exists by slug.
     *
     * @param slug the category slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Activate a category.
     *
     * @param id the category ID
     * @return the updated category
     */
    Category activate(Long id);

    /**
     * Deactivate a category.
     *
     * @param id the category ID
     * @return the updated category
     */
    Category deactivate(Long id);
}
