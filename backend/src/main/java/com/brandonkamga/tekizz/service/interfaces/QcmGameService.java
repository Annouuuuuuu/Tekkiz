package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.dto.qcm.QcmGameConfigRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmGameResultResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmGameSessionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmLeaderboardResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmQuestionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmUserStatsResponse;

/**
 * Service interface for QCM game operations.
 * Handles game session creation, question delivery, answer validation,
 * and dynamic difficulty management.
 */
public interface QcmGameService {

    /**
     * Create a new QCM game session with the given configuration.
     *
     * @param userId the user ID creating the session
     * @param config the game configuration
     * @return the created game session response
     */
    QcmGameSessionResponse createGameSession(Long userId, QcmGameConfigRequest config);

    /**
     * Get the next question for a game session.
     * Questions are selected based on difficulty progression.
     *
     * @param sessionId the game session ID
     * @return the next question to display
     */
    QcmQuestionResponse getNextQuestion(Long sessionId);

    /**
     * Submit an answer for the current question.
     * Updates score, lives, and difficulty level.
     *
     * @param sessionId the game session ID
     * @param request the answer submission request
     * @return the answer submission response with updated game state
     */
    QcmSubmitAnswerResponse submitAnswer(Long sessionId, QcmSubmitAnswerRequest request);

    /**
     * Get the final results of a completed game session.
     *
     * @param sessionId the game session ID
     * @return the game result response
     */
    QcmGameResultResponse getGameResults(Long sessionId);

    /**
     * Cancel/abandon a game session.
     *
     * @param sessionId the game session ID
     */
    void abandonGameSession(Long sessionId);

    /**
     * Get current game session state.
     *
     * @param sessionId the game session ID
     * @return the current game session state
     */
    QcmGameSessionResponse getSessionState(Long sessionId);

    /**
     * Get comprehensive QCM statistics for a user.
     *
     * @param userId the user ID
     * @return the user's QCM statistics
     */
    QcmUserStatsResponse getUserStats(Long userId);

    /**
     * Get QCM leaderboard.
     *
     * @param page the page number (0-indexed)
     * @param size the page size
     * @param categoryId optional category filter (null for all)
     * @param gameMode optional game mode filter (null or "ALL" for all modes)
     * @return the leaderboard response
     */
    QcmLeaderboardResponse getLeaderboard(int page, int size, Long categoryId, String gameMode);
}
