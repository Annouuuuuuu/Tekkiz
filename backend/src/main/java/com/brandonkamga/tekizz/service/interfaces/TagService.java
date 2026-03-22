package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Tag;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Tag operations.
 * Follows Interface Segregation Principle.
 */
public interface TagService {

    /**
     * Find a tag by its ID.
     *
     * @param id the tag ID
     * @return the tag if found
     */
    Optional<Tag> findById(Long id);

    /**
     * Find a tag by its name.
     *
     * @param name the tag name
     * @return the tag if found
     */
    Optional<Tag> findByName(String name);

    /**
     * Find a tag by its slug.
     *
     * @param slug the tag slug
     * @return the tag if found
     */
    Optional<Tag> findBySlug(String slug);

    /**
     * Find all active tags.
     *
     * @return list of active tags
     */
    List<Tag> findAllActive();

    /**
     * Find all tags.
     *
     * @return list of all tags
     */
    List<Tag> findAll();

    /**
     * Find tags by names.
     *
     * @param names list of tag names
     * @return list of tags matching the names
     */
    List<Tag> findByNameIn(List<String> names);

    /**
     * Find tags by slugs.
     *
     * @param slugs list of tag slugs
     * @return list of tags matching the slugs
     */
    List<Tag> findBySlugIn(List<String> slugs);

    /**
     * Save a tag.
     *
     * @param tag the tag to save
     * @return the saved tag
     */
    Tag save(Tag tag);

    /**
     * Delete a tag by its ID.
     *
     * @param id the tag ID
     */
    void deleteById(Long id);

    /**
     * Check if a tag exists by name.
     *
     * @param name the tag name
     * @return true if exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Check if a tag exists by slug.
     *
     * @param slug the tag slug
     * @return true if exists, false otherwise
     */
    boolean existsBySlug(String slug);

    /**
     * Activate a tag.
     *
     * @param id the tag ID
     * @return the updated tag
     */
    Tag activate(Long id);

    /**
     * Deactivate a tag.
     *
     * @param id the tag ID
     * @return the updated tag
     */
    Tag deactivate(Long id);
}
