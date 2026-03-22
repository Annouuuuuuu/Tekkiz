package com.brandonkamga.tekizz.service.interfaces;

import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.GameSession;
import com.brandonkamga.tekizz.domain.GameStatus;
import com.brandonkamga.tekizz.domain.GameType;
import com.brandonkamga.tekizz.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for GameSession operations.
 * Follows Interface Segregation Principle.
 */
public interface GameSessionService {

    /**
     * Find a game session by its ID.
     *
     * @param id the game session ID
     * @return the game session if found
     */
    Optional<GameSession> findById(Long id);

    /**
     * Find all game sessions.
     *
     * @return list of all game sessions
     */
    List<GameSession> findAll();

    /**
     * Find game sessions by user.
     *
     * @param user the user
     * @return list of game sessions for the user
     */
    List<GameSession> findByUser(User user);

    /**
     * Find game sessions by user ID.
     *
     * @param userId the user ID
     * @return list of game sessions for the user
     */
    List<GameSession> findByUserId(Long userId);

    /**
     * Find game sessions by user ordered by started date descending.
     *
     * @param user the user
     * @return list of game sessions ordered by started date
     */
    List<GameSession> findByUserOrderByStartedAtDesc(User user);

    /**
     * Find game sessions by user ID ordered by started date descending.
     *
     * @param userId the user ID
     * @return list of game sessions ordered by started date
     */
    List<GameSession> findByUserIdOrderByStartedAtDesc(Long userId);

    /**
     * Find game sessions by user and status.
     *
     * @param user the user
     * @param status the game status
     * @return list of game sessions
     */
    List<GameSession> findByUserAndStatus(User user, GameStatus status);

    /**
     * Find game sessions by user ID and status.
     *
     * @param userId the user ID
     * @param status the game status
     * @return list of game sessions
     */
    List<GameSession> findByUserIdAndStatus(Long userId, GameStatus status);

    /**
     * Find game sessions by user ID and status ordered by started date descending.
     *
     * @param userId the user ID
     * @param status the game status
     * @return list of game sessions ordered by started date
     */
    List<GameSession> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, GameStatus status);

    /**
     * Find game sessions by category.
     *
     * @param category the category
     * @return list of game sessions
     */
    List<GameSession> findByCategory(Category category);

    /**
     * Find game sessions by category ID.
     *
     * @param categoryId the category ID
     * @return list of game sessions
     */
    List<GameSession> findByCategoryId(Long categoryId);

    /**
     * Find game sessions by user and category ordered by started date descending.
     *
     * @param userId the user ID
     * @param categoryId the category ID
     * @return list of game sessions ordered by started date
     */
    List<GameSession> findByUserIdAndCategoryIdOrderByStartedAtDesc(Long userId, Long categoryId);

    /**
     * Find game sessions by game type.
     *
     * @param gameType the game type
     * @return list of game sessions
     */
    List<GameSession> findByGameType(GameType gameType);

    /**
     * Find game sessions by status.
     *
     * @param status the game status
     * @return list of game sessions
     */
    List<GameSession> findByStatus(GameStatus status);

    /**
     * Find game sessions started between two dates.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @return list of game sessions
     */
    List<GameSession> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find game sessions by user and started between two dates.
     *
     * @param userId the user ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of game sessions ordered by started date
     */
    List<GameSession> findByUserIdAndStartedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Count completed game sessions by user.
     *
     * @param userId the user ID
     * @return the count of completed game sessions
     */
    Long countCompletedByUserId(Long userId);

    /**
     * Sum total score for completed game sessions by user.
     *
     * @param userId the user ID
     * @return the sum of total scores
     */
    Long sumTotalScoreByUserId(Long userId);

    /**
     * Save a game session.
     *
     * @param gameSession the game session to save
     * @return the saved game session
     */
    GameSession save(GameSession gameSession);

    /**
     * Delete a game session by its ID.
     *
     * @param id the game session ID
     */
    void deleteById(Long id);

    /**
     * Check if a game session exists by ID.
     *
     * @param id the game session ID
     * @return true if exists, false otherwise
     */
    boolean existsById(Long id);
}
