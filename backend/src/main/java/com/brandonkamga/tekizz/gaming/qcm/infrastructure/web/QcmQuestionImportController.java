package com.brandonkamga.tekizz.gaming.qcm.infrastructure.web;

import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.importData.QuestionImportRequest;
import com.brandonkamga.tekizz.dto.importData.QuestionImportResponse;
import com.brandonkamga.tekizz.service.interfaces.QuestionImportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for importing questions from JSON files.
 * Admin-only endpoints for bulk question management.
 * Requires JWT token with ADMIN role.
 */
@RestController
@RequestMapping("/api/v1/admin/questions")
@PreAuthorize("hasRole('ADMIN')")
public class QcmQuestionImportController {

    private final QuestionImportService questionImportService;

    public QcmQuestionImportController(QuestionImportService questionImportService) {
        this.questionImportService = questionImportService;
    }

    /**
     * Import questions from JSON request body.
     * 
     * POST /api/v1/admin/questions/import
     * 
     * Requires: JWT token with ADMIN role
     * 
     * Request body should contain:
     * - categoryName: MUST exist in database (required)
     * - questions: array of questions with answers
     * - tags: created automatically if not exist (normalized to lowercase)
     * 
     * Example:
     * {
     *   "categoryName": "JavaScript",
     *   "questions": [
     *     {
     *       "content": "What is 'const' in JavaScript?",
     *       "hint": "Think about immutability",
     *       "explanation": "const declares a constant that cannot be reassigned",
     *       "difficulty": "EASY",
     *       "tags": ["javascript", "variables"],
     *       "answers": [
     *         {"content": "A variable that can change", "isCorrect": false},
     *         {"content": "A constant that cannot be reassigned", "isCorrect": true},
     *         {"content": "A function type", "isCorrect": false},
     *         {"content": "An operator", "isCorrect": false}
     *       ]
     *     }
     *   ]
     * }
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<QuestionImportResponse>> importQuestions(
            @Valid @RequestBody QuestionImportRequest request) {
        
        QuestionImportResponse response = questionImportService.importQuestions(request);
        
        String message = String.format("Import completed: %d imported, %d skipped", 
                response.getImported(), response.getSkipped());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response, message));
    }

    /**
     * Import questions from a JSON file on the server.
     * 
     * POST /api/v1/admin/questions/import-file
     * 
     * Requires: JWT token with ADMIN role
     * 
     * Request body should contain the file path:
     * {
     *   "filePath": "/path/to/questions.json"
     * }
     */
    @PostMapping("/import-file")
    public ResponseEntity<ApiResponse<QuestionImportResponse>> importQuestionsFromFile(
            @RequestBody FilePathRequest request) {
        
        QuestionImportResponse response = questionImportService.importQuestionsFromFile(request.getFilePath());
        
        String message = String.format("Import from file completed: %d imported, %d skipped", 
                response.getImported(), response.getSkipped());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(response, message));
    }

    /**
     * Simple request DTO for file path.
     */
    public static class FilePathRequest {
        private String filePath;

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
