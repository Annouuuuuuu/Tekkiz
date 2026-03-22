package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.domain.QuestionStatusType;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.QuestionRepository;
import com.brandonkamga.tekizz.service.interfaces.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of QuestionService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findAllActive() {
        return questionRepository.findByStatus(QuestionStatusType.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByCategory(Category category) {
        return questionRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByCategoryId(Long categoryId) {
        return questionRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByCategoryAndActive(Category category) {
        return questionRepository.findByCategoryAndStatusStatusName(category, QuestionStatusType.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByCategoryIdAndActive(Long categoryId) {
        return questionRepository.findByCategoryIdAndStatus(categoryId, QuestionStatusType.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByTagIdAndActive(Long tagId) {
        return questionRepository.findByTagId(tagId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findByTagSlugAndActive(String tagSlug) {
        // Since slug doesn't exist, we return all active questions
        return questionRepository.findByStatus(QuestionStatusType.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findRandomQuestions(int limit) {
        return questionRepository.findRandomQuestions(limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> findRandomQuestionsByCategory(Long categoryId, int limit) {
        return questionRepository.findRandomQuestionsByCategoryId(categoryId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveByCategory(Long categoryId) {
        return questionRepository.countByCategoryId(categoryId);
    }

    @Override
    public Question save(Question question) {
        return questionRepository.save(question);
    }

    @Override
    public void deleteById(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Question", "id", id);
        }
        questionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return questionRepository.existsById(id);
    }
}
