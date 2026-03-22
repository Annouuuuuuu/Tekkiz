package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Answer;
import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.Game;
import com.brandonkamga.tekizz.domain.GameTypeName;
import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.domain.QuestionLevel;
import com.brandonkamga.tekizz.domain.QuestionLevelType;
import com.brandonkamga.tekizz.domain.QuestionStatus;
import com.brandonkamga.tekizz.domain.QuestionStatusType;
import com.brandonkamga.tekizz.domain.Tag;
import com.brandonkamga.tekizz.dto.importData.QuestionImportRequest;
import com.brandonkamga.tekizz.dto.importData.QuestionImportResponse;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.AnswerRepository;
import com.brandonkamga.tekizz.repository.CategoryRepository;
import com.brandonkamga.tekizz.repository.GameRepository;
import com.brandonkamga.tekizz.repository.QuestionLevelRepository;
import com.brandonkamga.tekizz.repository.QuestionRepository;
import com.brandonkamga.tekizz.repository.QuestionStatusRepository;
import com.brandonkamga.tekizz.repository.TagRepository;
import com.brandonkamga.tekizz.service.interfaces.QuestionImportService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionImportService.
 * Handles importing questions from JSON with automatic tag creation.
 */
@Service
@Transactional
public class QuestionImportServiceImpl implements QuestionImportService {

    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionLevelRepository questionLevelRepository;
    private final QuestionStatusRepository questionStatusRepository;
    private final GameRepository gameRepository;
    private final ObjectMapper objectMapper;

    public QuestionImportServiceImpl(
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            QuestionLevelRepository questionLevelRepository,
            QuestionStatusRepository questionStatusRepository,
            GameRepository gameRepository,
            ObjectMapper objectMapper) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionLevelRepository = questionLevelRepository;
        this.questionStatusRepository = questionStatusRepository;
        this.gameRepository = gameRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public QuestionImportResponse importQuestions(QuestionImportRequest request) {
        List<QuestionImportResponse.ImportError> errors = new ArrayList<>();
        Set<String> createdTags = new HashSet<>();
        int imported = 0;
        int skipped = 0;

        System.out.println("    [DEBUG] Starting import for category: '" + request.getCategoryName() + "'");
        System.out.println("    [DEBUG] Number of questions to import: " + (request.getQuestions() != null ? request.getQuestions().size() : 0));

        // 1. Validate category exists (REQUIRED)
        Category category = categoryRepository.findByName(request.getCategoryName())
                .orElse(null);
        
        if (category == null) {
            System.out.println("    [ERROR] Category not found: '" + request.getCategoryName() + "'");
            // List available categories
            List<Category> allCategories = categoryRepository.findAll();
            System.out.println("    [DEBUG] Available categories: " + allCategories.stream().map(Category::getName).toList());
            return QuestionImportResponse.builder()
                    .success(false)
                    .imported(0)
                    .skipped(request.getQuestions() != null ? request.getQuestions().size() : 0)
                    .total(request.getQuestions() != null ? request.getQuestions().size() : 0)
                    .createdTags(new ArrayList<>())
                    .errors(List.of(QuestionImportResponse.ImportError.builder()
                            .questionIndex(0)
                            .questionContent("Category not found")
                            .error("Category '" + request.getCategoryName() + "' not found in database")
                            .build()))
                    .build();
        }
        System.out.println("    [DEBUG] Found category: " + category.getName() + " (ID: " + category.getId() + ")");

        // 2. Get QCM game type
        Game qcmGame = gameRepository.findByName(GameTypeName.QCM.name())
                .orElse(null);
        
        if (qcmGame == null) {
            System.out.println("    [ERROR] QCM Game not found");
            return QuestionImportResponse.builder()
                    .success(false)
                    .imported(0)
                    .skipped(request.getQuestions().size())
                    .total(request.getQuestions().size())
                    .createdTags(new ArrayList<>())
                    .errors(List.of(QuestionImportResponse.ImportError.builder()
                            .questionIndex(0)
                            .questionContent("Game not found")
                            .error("QCM Game not found in database")
                            .build()))
                    .build();
        }
        System.out.println("    [DEBUG] Found QCM game: " + qcmGame.getName() + " (ID: " + qcmGame.getId() + ")");

        // 3. Get default question status (ACTIVE)
        QuestionStatus activeStatus = questionStatusRepository.findByStatusName(QuestionStatusType.ACTIVE)
                .orElse(null);
        
        if (activeStatus == null) {
            System.out.println("    [ERROR] ACTIVE status not found");
            return QuestionImportResponse.builder()
                    .success(false)
                    .imported(0)
                    .skipped(request.getQuestions().size())
                    .total(request.getQuestions().size())
                    .createdTags(new ArrayList<>())
                    .errors(List.of(QuestionImportResponse.ImportError.builder()
                            .questionIndex(0)
                            .questionContent("Status not found")
                            .error("ACTIVE QuestionStatus not found in database")
                            .build()))
                    .build();
        }
        System.out.println("    [DEBUG] Found ACTIVE status: " + activeStatus.getStatusName());

        // 4. Process each question
        int questionIndex = 0;
        for (QuestionImportRequest.QuestionData questionData : request.getQuestions()) {
            questionIndex++;
            
            try {
                // Validate at least one correct answer
                long correctAnswersCount = questionData.getAnswers().stream()
                        .filter(a -> Boolean.TRUE.equals(a.getIsCorrect()))
                        .count();
                
                if (correctAnswersCount == 0) {
                    errors.add(QuestionImportResponse.ImportError.builder()
                            .questionIndex(questionIndex)
                            .questionContent(truncate(questionData.getContent(), 50))
                            .error("No correct answer provided")
                            .build());
                    skipped++;
                    continue;
                }

                // Get difficulty level
                QuestionLevel level = questionLevelRepository.findByLevelName(questionData.getDifficulty())
                        .orElseThrow(() -> new ResourceNotFoundException("QuestionLevel", "levelName", questionData.getDifficulty().name()));

                // Process tags (create if not exists, normalize to lowercase)
                Set<Tag> tags = processTags(questionData.getTags(), category, createdTags);

                // Create question
                Question question = Question.builder()
                        .content(questionData.getContent())
                        .hint(questionData.getHint())
                        .explanation(questionData.getExplanation())
                        .category(category)
                        .game(qcmGame)
                        .level(level)
                        .status(activeStatus)
                        .tags(tags)
                        .build();
                
                question = questionRepository.save(question);
                System.out.println("    [DEBUG] Saved question " + questionIndex + ": " + question.getId());

                // Create answers
                for (QuestionImportRequest.AnswerData answerData : questionData.getAnswers()) {
                    Answer answer = Answer.builder()
                            .question(question)
                            .content(answerData.getContent())
                            .isCorrect(answerData.getIsCorrect())
                            .isActive(true)
                            .build();
                    answerRepository.save(answer);
                }

                imported++;

            } catch (Exception e) {
                System.out.println("    [ERROR] Failed to import question " + questionIndex + ": " + e.getMessage());
                errors.add(QuestionImportResponse.ImportError.builder()
                        .questionIndex(questionIndex)
                        .questionContent(truncate(questionData.getContent(), 50))
                        .error(e.getMessage())
                        .build());
                skipped++;
            }
        }

        System.out.println("    [DEBUG] Import complete. Imported: " + imported + ", Skipped: " + skipped);

        return QuestionImportResponse.builder()
                .success(errors.isEmpty())
                .imported(imported)
                .skipped(skipped)
                .total(request.getQuestions().size())
                .createdTags(new ArrayList<>(createdTags))
                .errors(errors)
                .build();
    }

    @Override
    public QuestionImportResponse importQuestionsFromFile(String filePath) {
        try {
            File file = new File(filePath);
            QuestionImportRequest request = objectMapper.readValue(file, QuestionImportRequest.class);
            return importQuestions(request);
        } catch (Exception e) {
            return QuestionImportResponse.builder()
                    .success(false)
                    .imported(0)
                    .skipped(0)
                    .total(0)
                    .createdTags(new ArrayList<>())
                    .errors(List.of(QuestionImportResponse.ImportError.builder()
                            .questionIndex(0)
                            .questionContent("File import error")
                            .error("Failed to read file: " + e.getMessage())
                            .build()))
                    .build();
        }
    }

    @Override
    public QuestionImportResponse importQuestionsFromInputStream(InputStream inputStream, String filename) {
        try {
            QuestionImportRequest request = objectMapper.readValue(inputStream, QuestionImportRequest.class);
            
            // Debug: print the category name being searched
            System.out.println("  - Looking for category: '" + request.getCategoryName() + "'");
            
            return importQuestions(request);
        } catch (Exception e) {
            System.out.println("  - ERROR importing " + filename + ": " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return QuestionImportResponse.builder()
                    .success(false)
                    .imported(0)
                    .skipped(0)
                    .total(0)
                    .createdTags(new ArrayList<>())
                    .errors(List.of(QuestionImportResponse.ImportError.builder()
                            .questionIndex(0)
                            .questionContent("File import error")
                            .error("Failed to read " + filename + ": " + e.getMessage())
                            .build()))
                    .build();
        }
    }

    /**
     * Process tags: normalize to lowercase and create if not exists.
     */
    private Set<Tag> processTags(List<String> tagNames, Category category, Set<String> createdTags) {
        if (tagNames == null || tagNames.isEmpty()) {
            return new HashSet<>();
        }

        return tagNames.stream()
                .map(String::trim)
                .map(String::toLowerCase)  // Normalize to lowercase
                .filter(name -> !name.isEmpty())
                .map(tagName -> {
                    // Try to find existing tag
                    return tagRepository.findByName(tagName)
                            .orElseGet(() -> {
                                // Create new tag
                                Tag newTag = Tag.builder()
                                        .name(tagName)
                                        .slug(generateSlug(tagName))
                                        .category(category)
                                        .isActive(true)
                                        .build();
                                newTag = tagRepository.save(newTag);
                                createdTags.add(tagName);
                                return newTag;
                            });
                })
                .collect(Collectors.toSet());
    }

    /**
     * Generate a URL-friendly slug from tag name.
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Truncate string for error messages.
     */
    private String truncate(String str, int maxLength) {
        if (str == null) return "";
        return str.length() <= maxLength ? str : str.substring(0, maxLength) + "...";
    }
}
