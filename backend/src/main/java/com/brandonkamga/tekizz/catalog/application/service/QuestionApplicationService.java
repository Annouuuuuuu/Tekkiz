package com.brandonkamga.tekizz.catalog.application.service;

import com.brandonkamga.tekizz.catalog.application.port.in.CreateQuestionUseCase;
import com.brandonkamga.tekizz.catalog.application.port.in.SelectQuestionsForSessionUseCase;
import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionApplicationService implements CreateQuestionUseCase, SelectQuestionsForSessionUseCase {

    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final GameRepository gameRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;
    private final AnswerRepository answerRepository;
    private final TagRepository tagRepository;

    public QuestionApplicationService(QuestionRepository questionRepository,
                                       CategoryRepository categoryRepository,
                                       GameRepository gameRepository,
                                       QuestionLevelRepository questionLevelRepository,
                                       QuestionStatusRepository questionStatusRepository,
                                       AnswerRepository answerRepository,
                                       TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
        this.answerRepository = answerRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public Question create(CreateQuestionUseCase.CreateQuestionCommand command) {
        throw new UnsupportedOperationException("Use AdminQcmController for creating questions");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> selectForSession(Long categoryId, Long gameId, QuestionLevelType level,
                                            List<Long> tagIds, List<Long> excludeIds) {
        List<Question> questions;
        if (gameId != null) {
            questions = questionRepository.findByCategoryIdAndGameIdAndStatus(
                    categoryId, gameId, QuestionStatusType.ACTIVE);
        } else {
            questions = questionRepository.findByCategoryIdAndStatus(categoryId, QuestionStatusType.ACTIVE);
        }

        // Apply tag filter
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Question> tagFiltered = questionRepository.findByCategoryIdAndGameIdAndTagIdsAndStatus(
                    categoryId, gameId, tagIds, QuestionStatusType.ACTIVE);
            Set<Long> tagFilteredIds = tagFiltered.stream().map(Question::getId).collect(Collectors.toSet());
            questions = questions.stream()
                    .filter(q -> tagFilteredIds.contains(q.getId()))
                    .collect(Collectors.toList());
        }

        // Exclude already-answered questions
        if (excludeIds != null && !excludeIds.isEmpty()) {
            Set<Long> excludeSet = new HashSet<>(excludeIds);
            questions = questions.stream()
                    .filter(q -> !excludeSet.contains(q.getId()))
                    .collect(Collectors.toList());
        }

        return questions;
    }
}
