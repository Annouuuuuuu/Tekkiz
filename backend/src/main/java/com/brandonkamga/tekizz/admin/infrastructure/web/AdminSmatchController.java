package com.brandonkamga.tekizz.admin.infrastructure.web;

import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.admin.AdminSmatchDeckRequest;
import com.brandonkamga.tekizz.dto.admin.AdminSmatchPairRequest;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/smatch")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSmatchController {

    private final SmatchDeckRepository smatchDeckRepository;
    private final SmatchPairRepository smatchPairRepository;
    private final SmatchSessionRepository smatchSessionRepository;
    private final SmatchAttemptRepository smatchAttemptRepository;
    private final CategoryRepository categoryRepository;

    public AdminSmatchController(SmatchDeckRepository smatchDeckRepository,
                                 SmatchPairRepository smatchPairRepository,
                                 SmatchSessionRepository smatchSessionRepository,
                                 SmatchAttemptRepository smatchAttemptRepository,
                                 CategoryRepository categoryRepository) {
        this.smatchDeckRepository = smatchDeckRepository;
        this.smatchPairRepository = smatchPairRepository;
        this.smatchSessionRepository = smatchSessionRepository;
        this.smatchAttemptRepository = smatchAttemptRepository;
        this.categoryRepository = categoryRepository;
    }

    // ─── Decks ───────────────────────────────────────────────────────────────

    @GetMapping("/decks")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDecks(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SmatchDeck> deckPage = smatchDeckRepository.findFiltered(
                categoryId, isActive,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Map<String, Object>> decks = deckPage.getContent().stream()
                .filter(d -> difficulty == null || difficulty.isEmpty() || d.getDifficulty().equals(difficulty))
                .map(d -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", d.getId());
                    m.put("name", d.getName());
                    m.put("description", d.getDescription());
                    m.put("difficulty", d.getDifficulty());
                    m.put("isActive", d.getIsActive());
                    m.put("categoryId", d.getCategory() != null ? d.getCategory().getId() : null);
                    m.put("categoryName", d.getCategory() != null ? d.getCategory().getName() : null);
                    m.put("pairCount", smatchPairRepository.countByDeckId(d.getId()));
                    m.put("activePairCount", smatchPairRepository.countByDeckIdAndIsActiveTrue(d.getId()));
                    m.put("createdAt", d.getCreatedAt());
                    m.put("updatedAt", d.getUpdatedAt());
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", decks);
        result.put("totalElements", deckPage.getTotalElements());
        result.put("totalPages", deckPage.getTotalPages());
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/decks/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDeck(@PathVariable Long id) {
        SmatchDeck deck = smatchDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", id));

        List<SmatchPair> pairs = smatchPairRepository.findByDeckId(id);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", deck.getId());
        m.put("name", deck.getName());
        m.put("description", deck.getDescription());
        m.put("difficulty", deck.getDifficulty());
        m.put("isActive", deck.getIsActive());
        m.put("categoryId", deck.getCategory() != null ? deck.getCategory().getId() : null);
        m.put("categoryName", deck.getCategory() != null ? deck.getCategory().getName() : null);
        m.put("createdAt", deck.getCreatedAt());
        m.put("updatedAt", deck.getUpdatedAt());
        m.put("pairs", pairs.stream().map(p -> {
            var pm = new LinkedHashMap<String, Object>();
            pm.put("id", p.getId());
            pm.put("term", p.getTerm());
            pm.put("definition", p.getDefinition());
            pm.put("hint", p.getHint());
            pm.put("isActive", p.getIsActive());
            pm.put("createdAt", p.getCreatedAt());
            return pm;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(ApiResponse.success(m));
    }

    @PostMapping("/decks")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createDeck(
            @Valid @RequestBody AdminSmatchDeckRequest request) {

        if (smatchDeckRepository.findByName(request.getName()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("A deck with this name already exists"));
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        SmatchDeck deck = SmatchDeck.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(category)
                .difficulty(request.getDifficulty() != null ? request.getDifficulty().toUpperCase() : "EASY")
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        deck = smatchDeckRepository.save(deck);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", deck.getId());
        m.put("name", deck.getName());
        m.put("difficulty", deck.getDifficulty());
        m.put("isActive", deck.getIsActive());
        m.put("createdAt", deck.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.success(m, "Deck created successfully"));
    }

    @PutMapping("/decks/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateDeck(
            @PathVariable Long id,
            @Valid @RequestBody AdminSmatchDeckRequest request) {

        SmatchDeck deck = smatchDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", id));

        // Check name uniqueness if changed
        if (!deck.getName().equals(request.getName())) {
            if (smatchDeckRepository.findByName(request.getName()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("A deck with this name already exists"));
            }
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
        }

        deck.setName(request.getName());
        deck.setDescription(request.getDescription());
        deck.setCategory(category);
        if (request.getDifficulty() != null) deck.setDifficulty(request.getDifficulty().toUpperCase());
        if (request.getIsActive() != null) deck.setIsActive(request.getIsActive());

        deck = smatchDeckRepository.save(deck);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", deck.getId());
        m.put("name", deck.getName());
        m.put("difficulty", deck.getDifficulty());
        m.put("isActive", deck.getIsActive());
        m.put("updatedAt", deck.getUpdatedAt());

        return ResponseEntity.ok(ApiResponse.success(m, "Deck updated successfully"));
    }

    @PutMapping("/decks/{id}/status")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateDeckStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {

        SmatchDeck deck = smatchDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", id));

        Boolean isActive = body.get("isActive");
        if (isActive == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("isActive field is required"));
        }

        deck.setIsActive(isActive);
        smatchDeckRepository.save(deck);

        return ResponseEntity.ok(ApiResponse.success(null,
                "Deck " + (isActive ? "activated" : "deactivated") + " successfully"));
    }

    @DeleteMapping("/decks/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteDeck(@PathVariable Long id) {
        SmatchDeck deck = smatchDeckRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", id));

        long sessionCount = smatchSessionRepository.findByDeckId(id).size();
        if (sessionCount > 0) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot delete deck with " + sessionCount + " existing session(s). Deactivate it instead."));
        }

        smatchPairRepository.deleteByDeckId(id);
        smatchDeckRepository.delete(deck);

        return ResponseEntity.ok(ApiResponse.success(null, "Deck deleted successfully"));
    }

    // ─── Pairs ───────────────────────────────────────────────────────────────

    @GetMapping("/decks/{deckId}/pairs")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPairs(
            @PathVariable Long deckId,
            @RequestParam(required = false) Boolean isActive) {

        smatchDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", deckId));

        List<SmatchPair> pairs = isActive != null && isActive
                ? smatchPairRepository.findByDeckIdAndIsActiveTrue(deckId)
                : smatchPairRepository.findByDeckId(deckId);

        List<Map<String, Object>> result = pairs.stream().map(p -> {
            var m = new LinkedHashMap<String, Object>();
            m.put("id", p.getId());
            m.put("deckId", deckId);
            m.put("term", p.getTerm());
            m.put("definition", p.getDefinition());
            m.put("hint", p.getHint());
            m.put("isActive", p.getIsActive());
            m.put("createdAt", p.getCreatedAt());
            m.put("updatedAt", p.getUpdatedAt());
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/decks/{deckId}/pairs")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPair(
            @PathVariable Long deckId,
            @Valid @RequestBody AdminSmatchPairRequest request) {

        SmatchDeck deck = smatchDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", deckId));

        SmatchPair pair = SmatchPair.builder()
                .deck(deck)
                .term(request.getTerm())
                .definition(request.getDefinition())
                .hint(request.getHint())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        pair = smatchPairRepository.save(pair);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", pair.getId());
        m.put("deckId", deckId);
        m.put("term", pair.getTerm());
        m.put("definition", pair.getDefinition());
        m.put("hint", pair.getHint());
        m.put("isActive", pair.getIsActive());
        m.put("createdAt", pair.getCreatedAt());

        return ResponseEntity.ok(ApiResponse.success(m, "Pair created successfully"));
    }

    @PutMapping("/decks/{deckId}/pairs")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> replacePairs(
            @PathVariable Long deckId,
            @RequestBody List<@Valid AdminSmatchPairRequest> requests) {

        smatchDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", deckId));

        smatchPairRepository.deleteByDeckId(deckId);

        if (requests == null || requests.isEmpty()) {
            var m = new LinkedHashMap<String, Object>();
            m.put("created", 0);
            m.put("deckId", deckId);
            return ResponseEntity.ok(ApiResponse.success(m, "All pairs cleared"));
        }

        SmatchDeck deck = smatchDeckRepository.findById(deckId).get();
        List<SmatchPair> pairs = requests.stream().map(req -> SmatchPair.builder()
                .deck(deck)
                .term(req.getTerm())
                .definition(req.getDefinition())
                .hint(req.getHint())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build()
        ).collect(Collectors.toList());

        List<SmatchPair> saved = smatchPairRepository.saveAll(pairs);

        var m = new LinkedHashMap<String, Object>();
        m.put("created", saved.size());
        m.put("deckId", deckId);
        return ResponseEntity.ok(ApiResponse.success(m, saved.size() + " pairs saved"));
    }

    @PostMapping("/decks/{deckId}/pairs/bulk")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> createPairsBulk(
            @PathVariable Long deckId,
            @RequestBody List<@Valid AdminSmatchPairRequest> requests) {

        SmatchDeck deck = smatchDeckRepository.findById(deckId)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchDeck", "id", deckId));

        if (requests == null || requests.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("No pairs provided"));
        }

        List<SmatchPair> pairs = requests.stream().map(req -> SmatchPair.builder()
                .deck(deck)
                .term(req.getTerm())
                .definition(req.getDefinition())
                .hint(req.getHint())
                .isActive(req.getIsActive() != null ? req.getIsActive() : true)
                .build()
        ).collect(Collectors.toList());

        List<SmatchPair> saved = smatchPairRepository.saveAll(pairs);

        var m = new LinkedHashMap<String, Object>();
        m.put("created", saved.size());
        m.put("deckId", deckId);

        return ResponseEntity.ok(ApiResponse.success(m, saved.size() + " pairs created successfully"));
    }

    @PutMapping("/pairs/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> updatePair(
            @PathVariable Long id,
            @Valid @RequestBody AdminSmatchPairRequest request) {

        SmatchPair pair = smatchPairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchPair", "id", id));

        pair.setTerm(request.getTerm());
        pair.setDefinition(request.getDefinition());
        pair.setHint(request.getHint());
        if (request.getIsActive() != null) pair.setIsActive(request.getIsActive());

        pair = smatchPairRepository.save(pair);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", pair.getId());
        m.put("term", pair.getTerm());
        m.put("definition", pair.getDefinition());
        m.put("hint", pair.getHint());
        m.put("isActive", pair.getIsActive());
        m.put("updatedAt", pair.getUpdatedAt());

        return ResponseEntity.ok(ApiResponse.success(m, "Pair updated successfully"));
    }

    @DeleteMapping("/pairs/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deletePair(@PathVariable Long id) {
        SmatchPair pair = smatchPairRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchPair", "id", id));

        smatchPairRepository.delete(pair);
        return ResponseEntity.ok(ApiResponse.success(null, "Pair deleted successfully"));
    }

    // ─── Sessions ────────────────────────────────────────────────────────────

    @GetMapping("/sessions")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessions(
            @RequestParam(required = false) Long deckId,
            @RequestParam(required = false) String gameMode,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<SmatchSession> sessionPage = smatchSessionRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startedAt")));

        List<Map<String, Object>> sessions = sessionPage.getContent().stream()
                .filter(s -> deckId == null || (s.getDeck() != null && s.getDeck().getId().equals(deckId)))
                .filter(s -> gameMode == null || gameMode.isEmpty() || s.getGameMode().name().equals(gameMode))
                .filter(s -> completed == null || s.isCompleted() == completed)
                .map(s -> {
                    var m = new LinkedHashMap<String, Object>();
                    m.put("id", s.getId());
                    m.put("userId", s.getUser().getId());
                    m.put("username", s.getUser().getUsername());
                    m.put("deckId", s.getDeck() != null ? s.getDeck().getId() : null);
                    m.put("deckName", s.getDeck() != null ? s.getDeck().getName() : null);
                    m.put("gameMode", s.getGameMode().name());
                    m.put("gameModeDisplay", s.getGameMode().getDisplayName());
                    m.put("totalScore", s.getTotalScore());
                    m.put("pairsMatched", s.getPairsMatched());
                    m.put("wrongAttempts", s.getWrongAttempts());
                    m.put("livesRemaining", s.getLivesRemaining());
                    m.put("timerDuration", s.getTimerDuration());
                    m.put("completed", s.isCompleted());
                    m.put("startedAt", s.getStartedAt());
                    m.put("completedAt", s.getCompletedAt());
                    m.put("attemptCount", smatchAttemptRepository.countBySessionId(s.getId()));
                    return m;
                })
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", sessions);
        result.put("totalElements", sessionPage.getTotalElements());
        result.put("totalPages", sessionPage.getTotalPages());
        result.put("page", page);
        result.put("size", size);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/sessions/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSession(@PathVariable Long id) {
        SmatchSession session = smatchSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchSession", "id", id));

        var attempts = smatchAttemptRepository.findBySessionId(id);

        var m = new LinkedHashMap<String, Object>();
        m.put("id", session.getId());
        m.put("userId", session.getUser().getId());
        m.put("username", session.getUser().getUsername());
        m.put("deckId", session.getDeck() != null ? session.getDeck().getId() : null);
        m.put("deckName", session.getDeck() != null ? session.getDeck().getName() : null);
        m.put("gameMode", session.getGameMode().name());
        m.put("gameModeDisplay", session.getGameMode().getDisplayName());
        m.put("totalScore", session.getTotalScore());
        m.put("pairsMatched", session.getPairsMatched());
        m.put("wrongAttempts", session.getWrongAttempts());
        m.put("livesRemaining", session.getLivesRemaining());
        m.put("timerDuration", session.getTimerDuration());
        m.put("completed", session.isCompleted());
        m.put("startedAt", session.getStartedAt());
        m.put("completedAt", session.getCompletedAt());
        m.put("attempts", attempts.stream().map(a -> {
            var am = new LinkedHashMap<String, Object>();
            am.put("id", a.getId());
            am.put("pairId", a.getPair() != null ? a.getPair().getId() : null);
            am.put("pairTerm", a.getPair() != null ? a.getPair().getTerm() : null);
            am.put("isCorrect", a.getIsCorrect());
            am.put("timeTakenMs", a.getTimeTakenMs());
            am.put("pointsEarned", a.getPointsEarned());
            am.put("createdAt", a.getCreatedAt());
            return am;
        }).collect(Collectors.toList()));

        return ResponseEntity.ok(ApiResponse.success(m));
    }

    @DeleteMapping("/sessions/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteSession(@PathVariable Long id) {
        SmatchSession session = smatchSessionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SmatchSession", "id", id));

        smatchAttemptRepository.deleteBySessionId(id);
        smatchSessionRepository.delete(session);

        return ResponseEntity.ok(ApiResponse.success(null, "Session deleted successfully"));
    }

    // ─── Config ──────────────────────────────────────────────────────────────

    @GetMapping("/config")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConfig() {
        List<Map<String, Object>> modes = Arrays.stream(SmatchGameMode.values()).map(mode -> {
            var m = new LinkedHashMap<String, Object>();
            m.put("name", mode.name());
            m.put("displayName", mode.getDisplayName());
            m.put("initialTimeSeconds", mode.getInitialTimeSeconds());
            m.put("pointsPerCorrect", mode.getPointsPerCorrect());
            m.put("timeBonusSeconds", mode.getTimeBonusSeconds());
            m.put("timePenaltySeconds", mode.getTimePenaltySeconds());
            m.put("maxLives", mode.getMaxLives());
            m.put("hasTimer", mode.getInitialTimeSeconds() > 0);
            m.put("hasLives", mode.getMaxLives() > 0);
            return m;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(modes));
    }
}
