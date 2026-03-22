package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.QuestionStatus;
import com.brandonkamga.tekizz.domain.QuestionStatusType;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for QuestionStatus operations.
 * Follows Interface Segregation Principle.
 */
public interface QuestionStatusService {

    /**
     * Find a question status by its ID.
     *
     * @param id the question status ID
     * @return the question status if found
     */
    Optional<QuestionStatus> findById(Long id);

    /**
     * Find a question status by its name.
     *
     * @param statusName the status name
     * @return the question status if found
     */
    Optional<QuestionStatus> findByStatusName(QuestionStatusType statusName);

    /**
     * Find all question statuses.
     *
     * @return list of all question statuses
     */
    List<QuestionStatus> findAll();

    /**
     * Find all question statuses ordered by name.
     *
     * @return list of question statuses ordered by name
     */
    List<QuestionStatus> findAllOrderByName();

    /**
     * Save a question status.
     *
     * @param questionStatus the question status to save
     * @return the saved question status
     */
    QuestionStatus save(QuestionStatus questionStatus);

    /**
     * Delete a question status by its ID.
     *
     * @param id the question status ID
     */
    void deleteById(Long id);

    /**
     * Check if a question status exists by name.
     *
     * @param statusName the status name
     * @return true if exists, false otherwise
     */
    boolean existsByStatusName(QuestionStatusType statusName);

    /**
     * Check if a question status exists by ID.
     *
     * @param id the question status ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}
