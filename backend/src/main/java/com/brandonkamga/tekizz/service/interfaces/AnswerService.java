package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Answer;
import com.brandonkamga.tekizz.domain.Question;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Answer operations.
 * Follows Interface Segregation Principle.
 */
public interface AnswerService {

    /**
     * Find an answer by its ID.
     *
     * @param id the answer ID
     * @return the answer if found
     */
    Optional<Answer> findById(Long id);

    /**
     * Find all answers.
     *
     * @return list of all answers
     */
    List<Answer> findAll();

    /**
     * Find answers by question.
     *
     * @param question the question
     * @return list of answers for the question
     */
    List<Answer> findByQuestion(Question question);

    /**
     * Find answers by question ID.
     *
     * @param questionId the question ID
     * @return list of answers for the question
     */
    List<Answer> findByQuestionId(Long questionId);

    /**
     * Find correct answers for a question.
     *
     * @param questionId the question ID
     * @return list of correct answers
     */
    List<Answer> findCorrectAnswersByQuestionId(Long questionId);

    /**
     * Save an answer.
     *
     * @param answer the answer to save
     * @return the saved answer
     */
    Answer save(Answer answer);

    /**
     * Save all answers.
     *
     * @param answers the answers to save
     * @return the saved answers
     */
    List<Answer> saveAll(List<Answer> answers);

    /**
     * Delete an answer by its ID.
     *
     * @param id the answer ID
     */
    void deleteById(Long id);

    /**
     * Delete answers by question.
     *
     * @param question the question
     */
    void deleteByQuestion(Question question);

    /**
     * Check if an answer exists by ID.
     *
     * @param id the answer ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);

    /**
     * Activate an answer.
     *
     * @param id the answer ID
     * @return the updated answer
     */
    Answer activate(Long id);

    /**
     * Deactivate an answer.
     *
     * @param id the answer ID
     * @return the updated answer
     */
    Answer deactivate(Long id);
}
