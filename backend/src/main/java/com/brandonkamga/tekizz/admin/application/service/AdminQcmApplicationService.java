package com.brandonkamga.tekizz.admin.application.service;

import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Admin application service for QCM management.
 * Orchestrates admin operations on questions, categories, and tags.
 */
@Service
@Transactional
public class AdminQcmApplicationService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final GameRepository gameRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;

    public AdminQcmApplicationService(CategoryRepository categoryRepository,
                                       TagRepository tagRepository,
                                       QuestionRepository questionRepository,
                                       AnswerRepository answerRepository,
                                       GameRepository gameRepository,
                                       QuestionLevelRepository questionLevelRepository,
                                       QuestionStatusRepository questionStatusRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
    }

    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    public List<Question> getQuestions() {
        return questionRepository.findAll();
    }

    public void updateQuestionStatus(Long questionId, QuestionStatusType newStatus) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));
        QuestionStatus status = questionStatusRepository.findByStatusName(newStatus)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", newStatus));
        question.setStatus(status);
        questionRepository.save(question);
    }
}
