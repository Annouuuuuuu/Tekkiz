package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Answer;
import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.AnswerRepository;
import com.brandonkamga.tekizz.service.interfaces.AnswerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of AnswerService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Answer> findById(Long id) {
        return answerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Answer> findAll() {
        return answerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Answer> findByQuestion(Question question) {
        return answerRepository.findByQuestion(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Answer> findByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Answer> findCorrectAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionIdAndIsCorrectTrue(questionId);
    }

    @Override
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public List<Answer> saveAll(List<Answer> answers) {
        return answerRepository.saveAll(answers);
    }

    @Override
    public void deleteById(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Answer", "id", id);
        }
        answerRepository.deleteById(id);
    }

    @Override
    public void deleteByQuestion(Question question) {
        answerRepository.deleteByQuestionId(question.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return answerRepository.existsById(id);
    }

    @Override
    public Answer activate(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        answer.setIsActive(true);
        return answerRepository.save(answer);
    }

    @Override
    public Answer deactivate(Long id) {
        Answer answer = answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", id));
        answer.setIsActive(false);
        return answerRepository.save(answer);
    }
}
