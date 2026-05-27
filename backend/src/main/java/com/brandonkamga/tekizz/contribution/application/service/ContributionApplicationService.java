package com.brandonkamga.tekizz.contribution.application.service;

import com.brandonkamga.tekizz.contribution.application.port.in.*;
import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.dto.contribution.ContributionQuestionRequest;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Contribution bounded context application service.
 * Orchestrates contribution workflows (submit, review, withdraw).
 */
@Service
@Transactional
public class ContributionApplicationService
        implements SubmitContributionUseCase, WithdrawContributionUseCase, GetMyContributionsUseCase {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final GameRepository gameRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;

    public ContributionApplicationService(
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            GameRepository gameRepository,
            QuestionLevelRepository questionLevelRepository,
            QuestionStatusRepository questionStatusRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
    }

    @Override
    public Question submit(ContributionQuestionRequest req, User submittedBy) {
        boolean hasCorrect = req.getAnswers().stream()
                .anyMatch(ContributionQuestionRequest.AnswerRequest::getIsCorrect);
        if (!hasCorrect) throw new IllegalArgumentException("At least one answer must be correct");

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", req.getCategoryId()));
        Game game = gameRepository.findByName("QCM")
                .orElseThrow(() -> new ResourceNotFoundException("Game", "name", "QCM"));
        QuestionLevel level = questionLevelRepository
                .findByLevelName(QuestionLevelType.valueOf(req.getLevel()))
                .orElseThrow(() -> new ResourceNotFoundException("QuestionLevel", "name", req.getLevel()));
        QuestionStatus status = questionStatusRepository
                .findByStatusName(QuestionStatusType.REVIEW)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", "REVIEW"));

        Question question = Question.builder()
                .content(req.getContent())
                .explanation(req.getExplanation())
                .hint(req.getHint())
                .game(game)
                .category(category)
                .level(level)
                .status(status)
                .submittedBy(submittedBy)
                .build();

        question = questionRepository.save(question);

        for (ContributionQuestionRequest.AnswerRequest ar : req.getAnswers()) {
            Answer answer = Answer.builder()
                    .question(question)
                    .content(ar.getContent())
                    .isCorrect(ar.getIsCorrect())
                    .isActive(true)
                    .build();
            answerRepository.save(answer);
        }

        if (req.getTagIds() != null) {
            for (Long tagId : req.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(question.getTags()::add);
            }
            question = questionRepository.save(question);
        }

        return question;
    }

    @Override
    public void withdraw(Long questionId, Long userId) {
        Question q = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        if (q.getSubmittedBy() == null || !q.getSubmittedBy().getId().equals(userId)) {
            throw new org.springframework.security.access.AccessDeniedException("Not your submission");
        }
        if (q.getStatus().getStatusName() != QuestionStatusType.REVIEW) {
            throw new com.brandonkamga.tekizz.exception.BadRequestException("Only pending submissions can be withdrawn");
        }

        questionRepository.delete(q);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Question> getMyContributions(Long userId) {
        return questionRepository.findBySubmittedById(userId);
    }
}
