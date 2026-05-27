package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.GameSession;
import com.brandonkamga.tekizz.domain.GameStatus;
import com.brandonkamga.tekizz.domain.GameType;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.UserJpaEntity;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.GameSessionRepository;
import com.brandonkamga.tekizz.service.interfaces.GameSessionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of GameSessionService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionRepository gameSessionRepository;

    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GameSession> findById(Long id) {
        return gameSessionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findAll() {
        return gameSessionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUser(UserJpaEntity user) {
        return gameSessionRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserId(Long userId) {
        return gameSessionRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserOrderByStartedAtDesc(UserJpaEntity user) {
        return gameSessionRepository.findByUserOrderByStartedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserIdOrderByStartedAtDesc(Long userId) {
        return gameSessionRepository.findByUserIdOrderByStartedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserAndStatus(UserJpaEntity user, GameStatus status) {
        return gameSessionRepository.findByUserAndStatus(user, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserIdAndStatus(Long userId, GameStatus status) {
        return gameSessionRepository.findByUserIdAndStatus(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserIdAndStatusOrderByStartedAtDesc(Long userId, GameStatus status) {
        return gameSessionRepository.findByUserIdAndStatusOrderByStartedAtDesc(userId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByCategory(Category category) {
        return gameSessionRepository.findByCategory(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByCategoryId(Long categoryId) {
        return gameSessionRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserIdAndCategoryIdOrderByStartedAtDesc(Long userId, Long categoryId) {
        return gameSessionRepository.findByUserIdAndCategoryIdOrderByStartedAtDesc(userId, categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByGameType(GameType gameType) {
        return gameSessionRepository.findByGameType(gameType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByStatus(GameStatus status) {
        return gameSessionRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByStartedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return gameSessionRepository.findByStartedAtBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameSession> findByUserIdAndStartedAtBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return gameSessionRepository.findByUserIdAndStartedAtBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countCompletedByUserId(Long userId) {
        return gameSessionRepository.countCompletedByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long sumTotalScoreByUserId(Long userId) {
        return gameSessionRepository.sumTotalScoreByUserId(userId);
    }

    @Override
    public GameSession save(GameSession gameSession) {
        return gameSessionRepository.save(gameSession);
    }

    @Override
    public void deleteById(Long id) {
        if (!gameSessionRepository.existsById(id)) {
            throw new ResourceNotFoundException("GameSession", "id", id);
        }
        gameSessionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return gameSessionRepository.existsById(id);
    }
}
