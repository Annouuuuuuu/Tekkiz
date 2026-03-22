package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.Question;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Question operations.
 * Follows Interface Segregation Principle.
 */
public interface QuestionService {

    /**
     * Find a question by its ID.
     *
     * @param id the question ID
     * @return the question if found
     */
    Optional<Question> findById(Long id);

    /**
     * Find all questions.
     *
     * @return list of all questions
     */
    List<Question> findAll();

    /**
     * Find all active questions.
     *
     * @return list of active questions
     */
    List<Question> findAllActive();

    /**
     * Find questions by category.
     *
     * @param category the category
     * @return list of questions in the category
     */
    List<Question> findByCategory(Category category);

    /**
     * Find questions by category ID.
     *
     * @param categoryId the category ID
     * @return list of questions in the category
     */
    List<Question> findByCategoryId(Long categoryId);

    /**
     * Find active questions by category.
     *
     * @param category the category
     * @return list of active questions in the category
     */
    List<Question> findByCategoryAndActive(Category category);

    /**
     * Find active questions by category ID.
     *
     * @param categoryId the category ID
     * @return list of active questions in the category
     */
    List<Question> findByCategoryIdAndActive(Long categoryId);

    /**
     * Find questions by tag ID.
     *
     * @param tagId the tag ID
     * @return list of active questions with the tag
     */
    List<Question> findByTagIdAndActive(Long tagId);

    /**
     * Find questions by tag slug.
     *
     * @param tagSlug the tag slug
     * @return list of active questions with the tag
     */
    List<Question> findByTagSlugAndActive(String tagSlug);

    /**
     * Find random questions.
     *
     * @param limit the maximum number of questions to return
     * @return list of random active questions
     */
    List<Question> findRandomQuestions(int limit);

    /**
     * Find random questions by category.
     *
     * @param categoryId the category ID
     * @param limit the maximum number of questions to return
     * @return list of random active questions in the category
     */
    List<Question> findRandomQuestionsByCategory(Long categoryId, int limit);

    /**
     * Count active questions by category.
     *
     * @param categoryId the category ID
     * @return the count of active questions
     */
    Long countActiveByCategory(Long categoryId);

    /**
     * Save a question.
     *
     * @param question the question to save
     * @return the saved question
     */
    Question save(Question question);

    /**
     * Delete a question by its ID.
     *
     * @param id the question ID
     */
    void deleteById(Long id);

    /**
     * Check if a question exists by ID.
     *
     * @param id the question ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}
