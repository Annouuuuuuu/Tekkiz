package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.QuestionStatus;
import com.brandonkamga.tekizz.domain.QuestionStatusType;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.QuestionStatusRepository;
import com.brandonkamga.tekizz.service.interfaces.QuestionStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of QuestionStatusService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class QuestionStatusServiceImpl implements QuestionStatusService {

    private final QuestionStatusRepository questionStatusRepository;

    public QuestionStatusServiceImpl(QuestionStatusRepository questionStatusRepository) {
        this.questionStatusRepository = questionStatusRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionStatus> findById(Long id) {
        return questionStatusRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionStatus> findByStatusName(QuestionStatusType statusName) {
        return questionStatusRepository.findByStatusName(statusName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionStatus> findAll() {
        return questionStatusRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionStatus> findAllOrderByName() {
        return questionStatusRepository.findAllByOrderByStatusNameAsc();
    }

    @Override
    public QuestionStatus save(QuestionStatus questionStatus) {
        return questionStatusRepository.save(questionStatus);
    }

    @Override
    public void deleteById(Long id) {
        if (!questionStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("QuestionStatus", "id", id);
        }
        questionStatusRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByStatusName(QuestionStatusType statusName) {
        return questionStatusRepository.existsByStatusName(statusName);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return questionStatusRepository.existsById(id);
    }
}
