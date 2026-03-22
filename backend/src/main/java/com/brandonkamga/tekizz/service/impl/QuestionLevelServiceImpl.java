package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.QuestionLevel;
import com.brandonkamga.tekizz.domain.QuestionLevelType;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.QuestionLevelRepository;
import com.brandonkamga.tekizz.service.interfaces.QuestionLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of QuestionLevelService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class QuestionLevelServiceImpl implements QuestionLevelService {

    private final QuestionLevelRepository questionLevelRepository;

    public QuestionLevelServiceImpl(QuestionLevelRepository questionLevelRepository) {
        this.questionLevelRepository = questionLevelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionLevel> findById(Long id) {
        return questionLevelRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionLevel> findByLevelName(QuestionLevelType levelName) {
        return questionLevelRepository.findByLevelName(levelName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionLevel> findAll() {
        return questionLevelRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionLevel> findAllOrderByName() {
        return questionLevelRepository.findAllByOrderByLevelNameAsc();
    }

    @Override
    public QuestionLevel save(QuestionLevel questionLevel) {
        return questionLevelRepository.save(questionLevel);
    }

    @Override
    public void deleteById(Long id) {
        if (!questionLevelRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuestionLevel", "id", id);
        }
        questionLevelRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByLevelName(QuestionLevelType levelName) {
        return questionLevelRepository.existsByLevelName(levelName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return questionLevelRepository.existsById(id);
    }
}
