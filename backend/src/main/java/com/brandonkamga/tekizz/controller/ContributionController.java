package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.contribution.ContributionQuestionRequest;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import com.brandonkamga.tekizz.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/contributions")
public class ContributionController {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final GameRepository gameRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;
    private final UserService userService;

    public ContributionController(QuestionRepository questionRepository,
                                  AnswerRepository answerRepository,
                                  CategoryRepository categoryRepository,
                                  TagRepository tagRepository,
                                  GameRepository gameRepository,
                                  QuestionLevelRepository questionLevelRepository,
                                  QuestionStatusRepository questionStatusRepository,
                                  UserService userService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
        this.userService = userService;
    }

    @PostMapping("/questions")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitQuestion(
            @Valid @RequestBody ContributionQuestionRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);
        Question q = buildQuestion(request, user);
        Map<String, Object> result = toMap(q);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(result, "Question submitted for review"));
    }

    @PostMapping("/questions/bulk")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> submitQuestions(
            @Valid @RequestBody List<@Valid ContributionQuestionRequest> requests,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);
        int created = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            try {
                buildQuestion(requests.get(i), user);
                created++;
            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("submitted", created);
        result.put("errors", errors);
        return ResponseEntity.ok(ApiResponse.success(result,
                created + " question(s) submitted for review"));
    }

    @GetMapping("/questions/mine")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMySubmissions(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);
        List<Question> questions = questionRepository.findBySubmittedById(user.getId());
        List<Map<String, Object>> result = questions.stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .map(this::toMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> withdrawSubmission(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = getUser(userDetails);
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        if (q.getSubmittedBy() == null || !q.getSubmittedBy().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Not your submission"));
        }
        if (q.getStatus().getStatusName() != QuestionStatusType.REVIEW) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Only pending submissions can be withdrawn"));
        }

        questionRepository.delete(q);
        return ResponseEntity.ok(ApiResponse.success(null, "Submission withdrawn"));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private User getUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername());
    }

    private Question buildQuestion(ContributionQuestionRequest req, User submittedBy) {
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

    private Map<String, Object> toMap(Question q) {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", q.getId());
        m.put("content", q.getContent().length() > 100
                ? q.getContent().substring(0, 100) + "..." : q.getContent());
        m.put("categoryId", q.getCategory() != null ? q.getCategory().getId() : null);
        m.put("categoryName", q.getCategory() != null ? q.getCategory().getName() : null);
        m.put("level", q.getLevel().getLevelName().name());
        m.put("status", q.getStatus().getStatusName().name());
        m.put("answersCount", q.getAnswers().size());
        m.put("submittedBy", q.getSubmittedBy() != null ? q.getSubmittedBy().getUsername() : null);
        m.put("createdAt", q.getCreatedAt());
        return m;
    }
}
