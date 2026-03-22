package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.QuestionLevel;
import com.brandonkamga.tekizz.domain.QuestionLevelType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for QuestionLevel operations.
 * Follows Interface Segregation Principle.
 */
public interface QuestionLevelService {

    /**
     * Find a question level by its ID.
     *
     * @param id the question level ID
     * @return the question level if found
     */
    Optional<QuestionLevel> findById(Long id);

    /**
     * Find a question level by its name.
     *
     * @param levelName the level name
     * @return the question level if found
     */
    Optional<QuestionLevel> findByLevelName(QuestionLevelType levelName);

    /**
     * Find all question levels.
     *
     * @return list of all question levels
     */
    List<QuestionLevel> findAll();

    /**
     * Find all question levels ordered by name.
     *
     * @return list of question levels ordered by name
     */
    List<QuestionLevel> findAllOrderByName();

    /**
     * Save a question level.
     *
     * @param questionLevel the question level to save
     * @return the saved question level
     */
    QuestionLevel save(QuestionLevel questionLevel);

    /**
     * Delete a question level by its ID.
     *
     * @param id the question level ID
     */
    void deleteById(Long id);

    /**
     * Check if a question level exists by name.
     *
     * @param levelName the level name
     * @return true if exists, false otherwise
     */
    boolean existsByLevelName(QuestionLevelType levelName);

    /**
     * Check if a question level exists by ID.
     *
     * @param id the question level ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}
