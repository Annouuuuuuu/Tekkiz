package com.brandonkamga.tekizz.admin.infrastructure.web;

import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.admin.AdminCategoryRequest;
import com.brandonkamga.tekizz.dto.admin.AdminQuestionRequest;
import com.brandonkamga.tekizz.dto.admin.AdminTagRequest;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/qcm")
@PreAuthorize("hasRole('ADMIN')")
public class AdminQcmController {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final GameRepository gameRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;

    public AdminQcmController(CategoryRepository categoryRepository,
                               TagRepository tagRepository,
                               QuestionRepository questionRepository,
                               AnswerRepository answerRepository,
                               GameSessionRepository gameSessionRepository,
                               GameRepository gameRepository,
                               QuestionLevelRepository questionLevelRepository,
                               QuestionStatusRepository questionStatusRepository) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.gameRepository = gameRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
    }

    // ==================== CATEGORIES ====================

    @GetMapping("/categories")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getCategories() {
        List<Map<String, Object>> result = categoryRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Category::getDisplayOrder))
                .map(c -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", c.getId());
                    m.put("name", c.getName());
                    m.put("slug", c.getSlug());
                    m.put("description", c.getDescription());
                    m.put("displayOrder", c.getDisplayOrder());
                    m.put("isActive", c.getIsActive());
                    m.put("questionsCount", questionRepository.countByCategoryId(c.getId()));
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/categories")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCategory(
            @Valid @RequestBody AdminCategoryRequest request) {
        String slug = request.getSlug() != null && !request.getSlug().isBlank()
                ? request.getSlug()
                : toSlug(request.getName());
        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        category = categoryRepository.save(category);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(categoryToMap(category), "Category created"));
    }

    @PutMapping("/categories/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody AdminCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        if (request.getName() != null) category.setName(request.getName());
        if (request.getSlug() != null) category.setSlug(request.getSlug());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getDisplayOrder() != null) category.setDisplayOrder(request.getDisplayOrder());
        if (request.getIsActive() != null) category.setIsActive(request.getIsActive());
        category = categoryRepository.save(category);
        return ResponseEntity.ok(ApiResponse.success(categoryToMap(category), "Category updated"));
    }

    @DeleteMapping("/categories/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        long count = questionRepository.countByCategoryId(id);
        if (count > 0) {
            category.setIsActive(false);
            categoryRepository.save(category);
            return ResponseEntity.ok(ApiResponse.success(null, "Category deactivated (has " + count + " questions)"));
        }
        categoryRepository.delete(category);
        return ResponseEntity.ok(ApiResponse.success(null, "Category deleted"));
    }

    // ==================== TAGS ====================

    @GetMapping("/tags")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTags(
            @RequestParam(required = false) Long categoryId) {
        List<Tag> tags = categoryId != null
                ? tagRepository.findByCategoryId(categoryId)
                : tagRepository.findAll();
        List<Map<String, Object>> result = tags.stream().map(t -> {
            var m = new LinkedHashMap<String, Object>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("slug", t.getSlug());
            m.put("description", t.getDescription());
            m.put("isActive", t.getIsActive());
            m.put("categoryId", t.getCategory() != null ? t.getCategory().getId() : null);
            m.put("categoryName", t.getCategory() != null ? t.getCategory().getName() : null);
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/tags")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createTag(
            @Valid @RequestBody AdminTagRequest request) {
        Tag tag = Tag.builder()
                .name(request.getName())
                .slug(request.getSlug() != null ? request.getSlug() : toSlug(request.getName()))
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();
        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            tag.setCategory(cat);
        }
        tag = tagRepository.save(tag);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(tagToMap(tag), "Tag created"));
    }

    @PutMapping("/tags/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateTag(
            @PathVariable Long id, @Valid @RequestBody AdminTagRequest request) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        if (request.getName() != null) tag.setName(request.getName());
        if (request.getSlug() != null) tag.setSlug(request.getSlug());
        if (request.getDescription() != null) tag.setDescription(request.getDescription());
        if (request.getIsActive() != null) tag.setIsActive(request.getIsActive());
        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            tag.setCategory(cat);
        }
        tag = tagRepository.save(tag);
        return ResponseEntity.ok(ApiResponse.success(tagToMap(tag), "Tag updated"));
    }

    @DeleteMapping("/tags/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        tag.setIsActive(false);
        tagRepository.save(tag);
        return ResponseEntity.ok(ApiResponse.success(null, "Tag deactivated"));
    }

    // ==================== QUESTIONS ====================

    @GetMapping("/questions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String status) {
        List<Question> questions = questionRepository.findAll();
        List<Map<String, Object>> result = questions.stream()
                .filter(q -> categoryId == null || (q.getCategory() != null && q.getCategory().getId().equals(categoryId)))
                .filter(q -> level == null || q.getLevel().getLevelName().name().equals(level))
                .filter(q -> status == null || q.getStatus().getStatusName().name().equals(status))
                .map(q -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", q.getId());
                    m.put("content", q.getContent().length() > 120 ? q.getContent().substring(0, 120) + "..." : q.getContent());
                    m.put("categoryId", q.getCategory() != null ? q.getCategory().getId() : null);
                    m.put("categoryName", q.getCategory() != null ? q.getCategory().getName() : null);
                    m.put("level", q.getLevel().getLevelName().name());
                    m.put("status", q.getStatus().getStatusName().name());
                    m.put("answersCount", q.getAnswers().size());
                    m.put("tagsCount", q.getTags().size());
                    m.put("createdAt", q.getCreatedAt());
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/questions/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getQuestion(@PathVariable Long id) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        return ResponseEntity.ok(ApiResponse.success(questionToDetailMap(q)));
    }

    @PostMapping("/questions")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createQuestion(
            @Valid @RequestBody AdminQuestionRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        Game game = gameRepository.findByName("QCM")
                .orElseThrow(() -> new ResourceNotFoundException("Game", "name", "QCM"));
        QuestionLevel level = questionLevelRepository.findByLevelName(QuestionLevelType.valueOf(request.getLevel()))
                .orElseThrow(() -> new ResourceNotFoundException("QuestionLevel", "name", request.getLevel()));
        QuestionStatus status = questionStatusRepository.findByStatusName(
                QuestionStatusType.valueOf(request.getStatus() != null ? request.getStatus() : "ACTIVE"))
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", request.getStatus()));

        boolean hasCorrect = request.getAnswers().stream().anyMatch(AdminQuestionRequest.AnswerRequest::getIsCorrect);
        if (!hasCorrect) throw new BadRequestException("At least one answer must be marked as correct");

        Question question = Question.builder()
                .content(request.getContent())
                .explanation(request.getExplanation())
                .hint(request.getHint())
                .game(game)
                .category(category)
                .level(level)
                .status(status)
                .build();
        question = questionRepository.save(question);

        for (AdminQuestionRequest.AnswerRequest ar : request.getAnswers()) {
            Answer answer = Answer.builder()
                    .question(question)
                    .content(ar.getContent())
                    .isCorrect(ar.getIsCorrect())
                    .isActive(true)
                    .build();
            answerRepository.save(answer);
        }

        if (request.getTagIds() != null) {
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(question.getTags()::add);
            }
            questionRepository.save(question);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(questionToDetailMap(questionRepository.findById(question.getId()).get()), "Question created"));
    }

    @PutMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateQuestion(
            @PathVariable Long id, @Valid @RequestBody AdminQuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));

        if (request.getContent() != null) question.setContent(request.getContent());
        if (request.getExplanation() != null) question.setExplanation(request.getExplanation());
        if (request.getHint() != null) question.setHint(request.getHint());

        if (request.getCategoryId() != null) {
            Category cat = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            question.setCategory(cat);
        }
        if (request.getLevel() != null) {
            QuestionLevel level = questionLevelRepository.findByLevelName(QuestionLevelType.valueOf(request.getLevel()))
                    .orElseThrow(() -> new ResourceNotFoundException("QuestionLevel", "name", request.getLevel()));
            question.setLevel(level);
        }
        if (request.getStatus() != null) {
            QuestionStatus status = questionStatusRepository.findByStatusName(QuestionStatusType.valueOf(request.getStatus()))
                    .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", request.getStatus()));
            question.setStatus(status);
        }

        if (request.getAnswers() != null && !request.getAnswers().isEmpty()) {
            answerRepository.deleteAll(question.getAnswers());
            question.getAnswers().clear();
            for (AdminQuestionRequest.AnswerRequest ar : request.getAnswers()) {
                Answer answer = Answer.builder()
                        .question(question)
                        .content(ar.getContent())
                        .isCorrect(ar.getIsCorrect())
                        .isActive(true)
                        .build();
                answerRepository.save(answer);
            }
        }

        if (request.getTagIds() != null) {
            question.getTags().clear();
            for (Long tagId : request.getTagIds()) {
                tagRepository.findById(tagId).ifPresent(question.getTags()::add);
            }
        }

        question = questionRepository.save(question);
        return ResponseEntity.ok(ApiResponse.success(questionToDetailMap(question), "Question updated"));
    }

    @DeleteMapping("/questions/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(@PathVariable Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        questionRepository.delete(question);
        return ResponseEntity.ok(ApiResponse.success(null, "Question deleted"));
    }

    @PutMapping("/questions/{id}/status")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateQuestionStatus(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", id));
        QuestionStatusType next;
        String requested = body != null ? body.get("status") : null;
        if (requested != null && !requested.isBlank()) {
            next = QuestionStatusType.valueOf(requested.toUpperCase());
        } else {
            QuestionStatusType current = question.getStatus().getStatusName();
            next = current == QuestionStatusType.ACTIVE ? QuestionStatusType.ARCHIVED : QuestionStatusType.ACTIVE;
        }
        QuestionStatus newStatus = questionStatusRepository.findByStatusName(next)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", next));
        question.setStatus(newStatus);
        questionRepository.save(question);
        return ResponseEntity.ok(ApiResponse.success(null, "Status changed to " + next.name()));
    }

    @PostMapping("/questions/import")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> importQuestions(
            @Valid @RequestBody List<AdminQuestionRequest> requests) {
        int created = 0;
        List<String> errors = new ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            try {
                AdminQuestionRequest request = requests.get(i);
                Category category = categoryRepository.findById(request.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
                Game game = gameRepository.findByName("QCM")
                        .orElseThrow(() -> new ResourceNotFoundException("Game", "name", "QCM"));
                QuestionLevel level = questionLevelRepository.findByLevelName(QuestionLevelType.valueOf(request.getLevel()))
                        .orElseThrow(() -> new ResourceNotFoundException("QuestionLevel", "name", request.getLevel()));
                QuestionStatus status = questionStatusRepository.findByStatusName(
                        QuestionStatusType.valueOf(request.getStatus() != null ? request.getStatus() : "ACTIVE"))
                        .orElseThrow(() -> new ResourceNotFoundException("QuestionStatus", "name", request.getStatus()));
                boolean hasCorrect = request.getAnswers().stream().anyMatch(AdminQuestionRequest.AnswerRequest::getIsCorrect);
                if (!hasCorrect) throw new BadRequestException("No correct answer at row " + (i + 1));
                Question question = Question.builder()
                        .content(request.getContent())
                        .explanation(request.getExplanation())
                        .hint(request.getHint())
                        .game(game).category(category).level(level).status(status)
                        .build();
                question = questionRepository.save(question);
                for (AdminQuestionRequest.AnswerRequest ar : request.getAnswers()) {
                    answerRepository.save(Answer.builder().question(question).content(ar.getContent())
                            .isCorrect(ar.getIsCorrect()).isActive(true).build());
                }
                if (request.getTagIds() != null) {
                    for (Long tagId : request.getTagIds()) {
                        tagRepository.findById(tagId).ifPresent(question.getTags()::add);
                    }
                    questionRepository.save(question);
                }
                created++;
            } catch (Exception e) {
                errors.add("Row " + (i + 1) + ": " + e.getMessage());
            }
        }
        var result = new LinkedHashMap<String, Object>();
        result.put("imported", created);
        result.put("errors", errors);
        return ResponseEntity.ok(ApiResponse.success(result, created + " question(s) imported"));
    }

    // ==================== CONTRIBUTIONS ====================

    @GetMapping("/contributions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getContributions() {
        List<Map<String, Object>> result = questionRepository.findPendingContributions().stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .map(q -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", q.getId());
                    m.put("content", q.getContent());
                    m.put("explanation", q.getExplanation());
                    m.put("hint", q.getHint());
                    m.put("categoryId", q.getCategory() != null ? q.getCategory().getId() : null);
                    m.put("categoryName", q.getCategory() != null ? q.getCategory().getName() : null);
                    m.put("level", q.getLevel().getLevelName().name());
                    m.put("status", q.getStatus().getStatusName().name());
                    m.put("submittedBy", q.getSubmittedBy() != null ? q.getSubmittedBy().getUsername() : null);
                    m.put("submittedById", q.getSubmittedBy() != null ? q.getSubmittedBy().getId() : null);
                    m.put("createdAt", q.getCreatedAt());
                    m.put("answers", q.getAnswers().stream().map(a -> {
                        var am = new LinkedHashMap<String, Object>();
                        am.put("id", a.getId());
                        am.put("content", a.getContent());
                        am.put("isCorrect", a.getIsCorrect());
                        return am;
                    }).collect(Collectors.toList()));
                    m.put("tags", q.getTags().stream().map(t -> {
                        var tm = new LinkedHashMap<String, Object>();
                        tm.put("id", t.getId());
                        tm.put("name", t.getName());
                        return tm;
                    }).collect(Collectors.toList()));
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/contributions/count")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Long>>> getContributionsCount() {
        long count = questionRepository.countPendingContributions();
        return ResponseEntity.ok(ApiResponse.success(Map.of("pending", count)));
    }

    // ==================== SESSIONS ====================

    @GetMapping("/sessions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<Map<String, Object>> result = gameSessionRepository.findAll().stream()
                .sorted(Comparator.comparing(GameSession::getStartedAt).reversed())
                .skip((long) page * size).limit(size)
                .map(s -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", s.getId());
                    m.put("username", s.getUser().getUsername());
                    m.put("categoryName", s.getCategory() != null ? s.getCategory().getName() : "N/A");
                    m.put("gameMode", s.getGameMode() != null ? s.getGameMode().name() : "CLASSIC");
                    m.put("totalScore", s.getTotalScore());
                    m.put("questionsAnswered", s.getTotalQuestions());
                    m.put("livesRemaining", s.getLivesRemaining());
                    m.put("startedAt", s.getStartedAt());
                    m.put("completedAt", s.getCompletedAt());
                    m.put("status", s.getCompletedAt() != null ? "COMPLETED" : "IN_PROGRESS");
                    return m;
                }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/sessions/{id}/complete")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> forceCompleteSession(@PathVariable Long id) {
        GameSession session = gameSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", id));
        if (session.getCompletedAt() == null) {
            session.complete();
            gameSessionRepository.save(session);
        }
        return ResponseEntity.ok(ApiResponse.success(null, "Session force-completed"));
    }

    @DeleteMapping("/sessions/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        GameSession session = gameSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", id));
        gameSessionRepository.delete(session);
        return ResponseEntity.ok(ApiResponse.success(null, "Session deleted"));
    }

    // ==================== CONFIG ====================

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGameModeConfig() {
        List<Map<String, Object>> modes = Arrays.stream(GameMode.values()).map(m -> {
            var map = new LinkedHashMap<String, Object>();
            map.put("name", m.name());
            map.put("displayName", m.getDisplayName());
            map.put("initialTimeSeconds", m.getInitialTimeSeconds());
            map.put("maxTimeSeconds", m.getMaxTimeSeconds());
            map.put("timePenalty", m.getTimePenalty());
            map.put("pointsEasy", m.getPointsForDifficulty(QuestionLevelType.EASY));
            map.put("pointsMedium", m.getPointsForDifficulty(QuestionLevelType.MEDIUM));
            map.put("pointsHard", m.getPointsForDifficulty(QuestionLevelType.HARD));
            map.put("pointsExpert", m.getPointsForDifficulty(QuestionLevelType.EXPERT));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(modes));
    }

    // ==================== HELPERS ====================

    private String toSlug(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
    }

    private Map<String, Object> categoryToMap(Category c) {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", c.getId());
        m.put("name", c.getName());
        m.put("slug", c.getSlug());
        m.put("description", c.getDescription());
        m.put("displayOrder", c.getDisplayOrder());
        m.put("isActive", c.getIsActive());
        return m;
    }

    private Map<String, Object> tagToMap(Tag t) {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", t.getId());
        m.put("name", t.getName());
        m.put("slug", t.getSlug());
        m.put("description", t.getDescription());
        m.put("isActive", t.getIsActive());
        m.put("categoryId", t.getCategory() != null ? t.getCategory().getId() : null);
        m.put("categoryName", t.getCategory() != null ? t.getCategory().getName() : null);
        return m;
    }

    private Map<String, Object> questionToDetailMap(Question q) {
        var m = new LinkedHashMap<String, Object>();
        m.put("id", q.getId());
        m.put("content", q.getContent());
        m.put("explanation", q.getExplanation());
        m.put("hint", q.getHint());
        m.put("categoryId", q.getCategory() != null ? q.getCategory().getId() : null);
        m.put("categoryName", q.getCategory() != null ? q.getCategory().getName() : null);
        m.put("level", q.getLevel().getLevelName().name());
        m.put("status", q.getStatus().getStatusName().name());
        m.put("createdAt", q.getCreatedAt());
        m.put("answers", q.getAnswers().stream().map(a -> {
            var am = new LinkedHashMap<String, Object>();
            am.put("id", a.getId());
            am.put("content", a.getContent());
            am.put("isCorrect", a.getIsCorrect());
            am.put("isActive", a.getIsActive());
            return am;
        }).collect(Collectors.toList()));
        m.put("tags", q.getTags().stream().map(t -> {
            var tm = new LinkedHashMap<String, Object>();
            tm.put("id", t.getId());
            tm.put("name", t.getName());
            return tm;
        }).collect(Collectors.toList()));
        return m;
    }
}
