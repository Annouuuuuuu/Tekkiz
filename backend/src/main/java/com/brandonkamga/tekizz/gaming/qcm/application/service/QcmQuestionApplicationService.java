package com.brandonkamga.tekizz.gaming.qcm.application.service;

import com.brandonkamga.tekizz.domain.QuestionStatusType;
import com.brandonkamga.tekizz.gaming.qcm.application.port.in.CreateQcmQuestionUseCase;
import com.brandonkamga.tekizz.gaming.qcm.application.port.in.SelectQcmQuestionsUseCase;
import com.brandonkamga.tekizz.gaming.qcm.domain.model.QcmAnswer;
import com.brandonkamga.tekizz.gaming.qcm.domain.model.QcmQuestion;
import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.QcmQuestionLevel;
import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.QcmQuestionStatus;
import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QcmQuestionApplicationService implements CreateQcmQuestionUseCase, SelectQcmQuestionsUseCase {

    private final com.brandonkamga.tekizz.repository.QuestionRepository questionRepository;
    private final com.brandonkamga.tekizz.repository.CategoryRepository categoryRepository;
    private final com.brandonkamga.tekizz.repository.GameRepository gameRepository;
    private final com.brandonkamga.tekizz.repository.QuestionLevelRepository questionLevelRepository;
    private final com.brandonkamga.tekizz.repository.QuestionStatusRepository questionStatusRepository;
    private final com.brandonkamga.tekizz.repository.AnswerRepository answerRepository;
    private final com.brandonkamga.tekizz.repository.TagRepository tagRepository;

    public QcmQuestionApplicationService(
            com.brandonkamga.tekizz.repository.QuestionRepository questionRepository,
            com.brandonkamga.tekizz.repository.CategoryRepository categoryRepository,
            com.brandonkamga.tekizz.repository.GameRepository gameRepository,
            com.brandonkamga.tekizz.repository.QuestionLevelRepository questionLevelRepository,
            com.brandonkamga.tekizz.repository.QuestionStatusRepository questionStatusRepository,
            com.brandonkamga.tekizz.repository.AnswerRepository answerRepository,
            com.brandonkamga.tekizz.repository.TagRepository tagRepository) {
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
        this.answerRepository = answerRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public QcmQuestion create(CreateQcmQuestionUseCase.CreateQcmQuestionCommand command) {
        throw new UnsupportedOperationException("Use AdminQcmController for creating QCM questions");
    }

    @Override
    @Transactional(readOnly = true)
    public List<QcmQuestion> selectForSession(Long categoryId, Long gameId, QcmQuestionLevel level,
                                              List<Long> tagIds, List<Long> excludeIds) {
        List<com.brandonkamga.tekizz.domain.Question> questions;
        if (gameId != null) {
            questions = questionRepository.findByCategoryIdAndGameIdAndStatus(
                    categoryId, gameId, QuestionStatusType.ACTIVE);
        } else {
            questions = questionRepository.findByCategoryIdAndStatus(categoryId, QuestionStatusType.ACTIVE);
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            List<com.brandonkamga.tekizz.domain.Question> tagFiltered =
                    questionRepository.findByCategoryIdAndGameIdAndTagIdsAndStatus(
                            categoryId, gameId, tagIds, QuestionStatusType.ACTIVE);
            Set<Long> tagFilteredIds = tagFiltered.stream()
                    .map(com.brandonkamga.tekizz.domain.Question::getId)
                    .collect(Collectors.toSet());
            questions = questions.stream()
                    .filter(q -> tagFilteredIds.contains(q.getId()))
                    .collect(Collectors.toList());
        }

        if (excludeIds != null && !excludeIds.isEmpty()) {
            Set<Long> excludeSet = new HashSet<>(excludeIds);
            questions = questions.stream()
                    .filter(q -> !excludeSet.contains(q.getId()))
                    .collect(Collectors.toList());
        }

        return questions.stream()
                .map(this::toQcmQuestion)
                .collect(Collectors.toList());
    }

    private QcmQuestion toQcmQuestion(com.brandonkamga.tekizz.domain.Question q) {
        List<QcmAnswer> answers = q.getAnswers().stream()
                .map(a -> QcmAnswer.reconstitute(
                        a.getId(),
                        q.getId(),
                        a.getContent(),
                        Boolean.TRUE.equals(a.getIsCorrect()),
                        null,
                        null))
                .collect(Collectors.toList());

        QcmQuestionLevel qcmLevel = mapLevel(q.getLevel().getLevelName().name());
        QcmQuestionStatus qcmStatus = mapStatus(q.getStatus().getStatusName().name());
        Long catId = q.getCategory() != null ? q.getCategory().getId() : null;

        return QcmQuestion.reconstitute(q.getId(), q.getContent(), q.getExplanation(),
                q.getHint(), qcmLevel, qcmStatus, catId, answers);
    }

    private QcmQuestionLevel mapLevel(String name) {
        try { return QcmQuestionLevel.valueOf(name); }
        catch (IllegalArgumentException e) { return QcmQuestionLevel.EASY; }
    }

    private QcmQuestionStatus mapStatus(String name) {
        try { return QcmQuestionStatus.valueOf(name); }
        catch (IllegalArgumentException e) { return QcmQuestionStatus.ACTIVE; }
    }
}
