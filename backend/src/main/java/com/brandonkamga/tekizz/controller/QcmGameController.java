package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmGameConfigRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmGameResultResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmGameSessionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmLeaderboardResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmQuestionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmUserStatsResponse;
import com.brandonkamga.tekizz.service.interfaces.QcmGameService;
import com.brandonkamga.tekizz.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for QCM game operations.
 * Provides endpoints for:
 * - Creating game sessions
 * - Getting questions
 * - Submitting answers
 * - Retrieving results
 * - User statistics
 * - Leaderboard
 */
@RestController
@RequestMapping("/api/v1/games/qcm")
public class QcmGameController {

    private final QcmGameService qcmGameService;
    private final UserService userService;

    public QcmGameController(QcmGameService qcmGameService, UserService userService) {
        this.qcmGameService = qcmGameService;
        this.userService = userService;
    }

    /**
     * Create a new QCM game session.
     * 
     * POST /api/v1/games/qcm/sessions
     * 
     * Request body:
     * - categoryId: ID of the category for questions
     * - tagIds: Optional list of tag IDs to filter questions
     * - numberOfQuestions: Number of questions in the game (5-50)
     * - lives: Number of lives (default: 3)
     * - showHints: Whether to show hints (default: true)
     * - showExplanations: Whether to show explanations (default: true)
     * - timeLimitPerQuestion: Time limit per question in seconds (default: 30)
     */
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<QcmGameSessionResponse>> createGameSession(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QcmGameConfigRequest request) {
        
        Long userId = extractUserId(userDetails);
        QcmGameSessionResponse response = qcmGameService.createGameSession(userId, request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Game session created successfully"));
    }

    /**
     * Get the next question in a game session.
     * 
     * GET /api/v1/games/qcm/sessions/{sessionId}/questions/next
     * 
     * Returns the next question to display to the user.
     * Does not reveal correct answers to prevent cheating.
     */
    @GetMapping("/sessions/{sessionId}/questions/next")
    public ResponseEntity<ApiResponse<QcmQuestionResponse>> getNextQuestion(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        validateSessionOwnership(sessionId, userDetails);
        QcmQuestionResponse response = qcmGameService.getNextQuestion(sessionId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Question retrieved successfully"));
    }

    /**
     * Submit an answer for the current question.
     * 
     * POST /api/v1/games/qcm/sessions/{sessionId}/answers
     * 
     * Request body:
     * - questionId: ID of the question being answered
     * - selectedAnswerId: ID of the selected answer
     * - timeTakenSeconds: Time taken to answer (optional)
     * - usedHint: Whether the user used a hint (optional)
     * 
     * Returns:
     * - Whether the answer was correct
     * - Explanation of the correct answer
     * - Updated game state (score, lives, etc.)
     * - Whether the game is over or has more questions
     */
    @PostMapping("/sessions/{sessionId}/answers")
    public ResponseEntity<ApiResponse<QcmSubmitAnswerResponse>> submitAnswer(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody QcmSubmitAnswerRequest request) {
        
        validateSessionOwnership(sessionId, userDetails);
        QcmSubmitAnswerResponse response = qcmGameService.submitAnswer(sessionId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Answer submitted successfully"));
    }

    /**
     * Get the final results of a completed game session.
     * 
     * GET /api/v1/games/qcm/sessions/{sessionId}/results
     * 
     * Returns comprehensive game results including:
     * - Total score and accuracy
     * - Question-by-question review
     * - Performance analysis
     * - Difficulty progression
     */
    @GetMapping("/sessions/{sessionId}/results")
    public ResponseEntity<ApiResponse<QcmGameResultResponse>> getGameResults(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        validateSessionOwnership(sessionId, userDetails);
        QcmGameResultResponse response = qcmGameService.getGameResults(sessionId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Game results retrieved successfully"));
    }

    /**
     * Get the current state of a game session.
     * 
     * GET /api/v1/games/qcm/sessions/{sessionId}
     * 
     * Returns current session state including:
     * - Current question index
     * - Lives remaining
     * - Session status
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<QcmGameSessionResponse>> getSessionState(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        validateSessionOwnership(sessionId, userDetails);
        QcmGameSessionResponse response = qcmGameService.getSessionState(sessionId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Session state retrieved successfully"));
    }

    /**
     * Abandon a game session.
     * 
     * DELETE /api/v1/games/qcm/sessions/{sessionId}
     * 
     * Marks the session as cancelled and ends the game.
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<Void>> abandonGameSession(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        validateSessionOwnership(sessionId, userDetails);
        qcmGameService.abandonGameSession(sessionId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Game session abandoned successfully"));
    }

    /**
     * Get comprehensive QCM statistics for the authenticated user.
     * 
     * GET /api/v1/games/qcm/stats
     * 
     * Returns:
     * - Total games played, questions answered
     * - Accuracy, best score, average score
     * - Recent performance trends
     * - Category breakdown
     * - Leaderboard position
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<QcmUserStatsResponse>> getUserStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Long userId = extractUserId(userDetails);
        QcmUserStatsResponse response = qcmGameService.getUserStats(userId);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Stats retrieved successfully"));
    }

    /**
     * Get QCM leaderboard.
     * 
     * GET /api/v1/games/qcm/leaderboard
     * 
     * Parameters:
     * - page: Page number (0-indexed, default: 0)
     * - size: Page size (default: 10)
     * - categoryId: Optional category filter
     * - gameMode: Optional game mode filter (BLITZ, RUSH, CLASSIC, or ALL)
     * 
     * Returns ranked players with their QCM-specific stats.
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<ApiResponse<QcmLeaderboardResponse>> getLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "ALL") String gameMode) {
        
        QcmLeaderboardResponse response = qcmGameService.getLeaderboard(page, size, categoryId, gameMode);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Leaderboard retrieved successfully"));
    }

    // ==================== Helper Methods ====================

    private Long extractUserId(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("User not authenticated");
        }
        // The username in UserDetails is the email address
        User user = userService.findByEmail(userDetails.getUsername());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        return user.getId();
    }

    private void validateSessionOwnership(Long sessionId, UserDetails userDetails) {
        // In a real implementation, you would verify that the authenticated user
        // owns the game session they're trying to access
        // This is a placeholder for that validation
        if (userDetails == null) {
            throw new IllegalStateException("User not authenticated");
        }
    }
}
