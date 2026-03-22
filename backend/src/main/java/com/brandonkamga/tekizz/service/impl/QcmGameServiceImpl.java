package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Answer;
import com.brandonkamga.tekizz.domain.Category;
import com.brandonkamga.tekizz.domain.Game;
import com.brandonkamga.tekizz.domain.GameMode;
import com.brandonkamga.tekizz.domain.GameSession;
import com.brandonkamga.tekizz.domain.GameStatus;
import com.brandonkamga.tekizz.domain.GameStatusType;
import com.brandonkamga.tekizz.domain.GameTypeName;
import com.brandonkamga.tekizz.domain.Question;
import com.brandonkamga.tekizz.domain.QuestionLevel;
import com.brandonkamga.tekizz.domain.QuestionLevelType;
import com.brandonkamga.tekizz.domain.QuestionStatusType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.domain.UserAnswer;
import com.brandonkamga.tekizz.dto.qcm.QcmGameConfigRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmGameResultResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmGameSessionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmLeaderboardResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmQuestionResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerRequest;
import com.brandonkamga.tekizz.dto.qcm.QcmSubmitAnswerResponse;
import com.brandonkamga.tekizz.dto.qcm.QcmUserStatsResponse;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.AnswerRepository;
import com.brandonkamga.tekizz.repository.CategoryRepository;
import com.brandonkamga.tekizz.repository.GameRepository;
import com.brandonkamga.tekizz.repository.GameSessionRepository;
import com.brandonkamga.tekizz.repository.GameStatusRepository;
import com.brandonkamga.tekizz.repository.QuestionRepository;
import com.brandonkamga.tekizz.repository.UserAnswerRepository;
import com.brandonkamga.tekizz.repository.UserRepository;
import com.brandonkamga.tekizz.service.interfaces.QcmGameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of QcmGameService.
 * Handles complete QCM game logic including:
 * - Session creation with configuration
 * - Dynamic question selection based on categories/tags
 * - Progressive difficulty management based on game mode
 * - Answer validation and scoring
 * - Game state management
 * 
 * Game Modes:
 * - BLITZ: 60s initial, 90s max, high points, fast progression
 * - RUSH: 120s initial, 150s max, medium points, medium progression
 * - CLASSIC: 300s initial, 360s max, low points, slow progression
 */
@Service
@Transactional
public class QcmGameServiceImpl implements QcmGameService {

    private final GameSessionRepository gameSessionRepository;
    private final GameRepository gameRepository;
    private final GameStatusRepository gameStatusRepository;
    private final CategoryRepository categoryRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserAnswerRepository userAnswerRepository;
    private final UserRepository userRepository;

    public QcmGameServiceImpl(
            GameSessionRepository gameSessionRepository,
            GameRepository gameRepository,
            GameStatusRepository gameStatusRepository,
            CategoryRepository categoryRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            UserAnswerRepository userAnswerRepository,
            UserRepository userRepository) {
        this.gameSessionRepository = gameSessionRepository;
        this.gameRepository = gameRepository;
        this.gameStatusRepository = gameStatusRepository;
        this.categoryRepository = categoryRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userAnswerRepository = userAnswerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public QcmGameSessionResponse createGameSession(Long userId, QcmGameConfigRequest config) {
        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Validate category
        Category category = categoryRepository.findById(config.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", config.getCategoryId()));

        // Get QCM game type
        Game qcmGame = gameRepository.findByName(GameTypeName.QCM.name())
                .orElseThrow(() -> new ResourceNotFoundException("Game", "name", GameTypeName.QCM.name()));

        // Get IN_PROGRESS status
        GameStatus inProgressStatus = gameStatusRepository.findByStatusName(GameStatusType.IN_PROGRESS)
                .orElseThrow(() -> new ResourceNotFoundException("GameStatus", "statusName", GameStatusType.IN_PROGRESS));

        // Get game mode from config
        GameMode gameMode = config.getGameMode();

        // Create game session with game mode configuration
        // Everyone starts at EASY difficulty - progression is score-based per mode
        // Default to 3 lives if not specified
        int lives = config.getLives() != null ? config.getLives() : 3;
        
        GameSession session = GameSession.builder()
                .user(user)
                .game(qcmGame)
                .category(category)
                .status(inProgressStatus)
                .gameMode(gameMode)
                .livesRemaining(lives)
                .globalTimerDuration(gameMode.getInitialTimeSeconds())
                .maxTimerDuration(gameMode.getMaxTimeSeconds())
                .startedAt(LocalDateTime.now())
                .build();

        session = gameSessionRepository.save(session);

        // Survival mode - questions are fetched dynamically as the game progresses
        // No pre-selection of questions

        return QcmGameSessionResponse.builder()
                .sessionId(session.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .livesRemaining(session.getLivesRemaining())
                .currentQuestionIndex(0)
                .globalTimerDuration(session.getGlobalTimerDuration())
                .startedAt(session.getStartedAt())
                .status(GameStatusType.IN_PROGRESS.name())
                .currentDifficulty(QuestionLevelType.EASY.name()) // Everyone starts at EASY
                .gameMode(gameMode.name())
                .maxTimerDuration(gameMode.getMaxTimeSeconds())
                .build();
    }

    @Override
    public QcmQuestionResponse getNextQuestion(Long sessionId) {
        GameSession session = getValidSession(sessionId);

        if (session.isGameOver() || session.getCompletedAt() != null) {
            throw new BadRequestException("Game session is already completed");
        }

        // Get already answered questions
        List<UserAnswer> answeredQuestions = userAnswerRepository.findByGameSessionId(sessionId);
        int currentQuestionIndex = answeredQuestions.size();

        // Survival mode - no limit on questions, game ends only by timer or lives

        // Get game mode for difficulty determination
        GameMode gameMode = session.getGameMode();
        if (gameMode == null) {
            gameMode = GameMode.CLASSIC; // Default fallback
        }

        // Determine current difficulty level based on current score and game mode
        QuestionLevelType currentDifficulty = determineCurrentDifficulty(session.getTotalScore(), gameMode);

        // Get next question based on difficulty and filters
        Question question = selectNextQuestion(session, answeredQuestions, currentDifficulty);

        // Get answers and shuffle them
        List<Answer> answers = answerRepository.findByQuestionIdAndIsActiveTrue(question.getId());
        Collections.shuffle(answers);

        // Build response without revealing correct answers
        // Survival mode: only show current question number, not total
        return QcmQuestionResponse.builder()
                .questionId(question.getId())
                .questionIndex(currentQuestionIndex + 1)
                .content(question.getContent())
                .hint(question.getHint())
                .showHint(question.getHint() != null && !question.getHint().isEmpty())
                .difficultyLevel(question.getLevel().getLevelName().name())
                .answers(answers.stream()
                        .map(a -> QcmQuestionResponse.AnswerOption.builder()
                                .answerId(a.getId())
                                .content(a.getContent())
                                .displayOrder(answers.indexOf(a) + 1)
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public QcmSubmitAnswerResponse submitAnswer(Long sessionId, QcmSubmitAnswerRequest request) {
        GameSession session = getValidSession(sessionId);

        // Validate question
        Question question = questionRepository.findById(request.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", request.getQuestionId()));

        // Check if question already answered in this session
        boolean alreadyAnswered = userAnswerRepository.findByGameSessionId(sessionId).stream()
                .anyMatch(ua -> ua.getQuestion().getId().equals(request.getQuestionId()));
        if (alreadyAnswered) {
            throw new BadRequestException("Question already answered in this session");
        }

        // Validate answer
        Answer selectedAnswer = answerRepository.findById(request.getSelectedAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer", "id", request.getSelectedAnswerId()));

        if (!selectedAnswer.getQuestion().getId().equals(question.getId())) {
            throw new BadRequestException("Answer does not belong to the specified question");
        }

        // Get correct answer
        Answer correctAnswer = answerRepository.findByQuestionIdAndIsCorrectTrue(question.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("No correct answer found for question"));

        boolean isCorrect = selectedAnswer.getIsCorrect();
        QuestionLevelType questionDifficulty = question.getLevel().getLevelName();

        // Get game mode for scoring calculations
        GameMode gameMode = session.getGameMode();
        if (gameMode == null) {
            gameMode = GameMode.CLASSIC; // Default fallback
        }

        // Calculate points and time adjustment based on game mode
        int pointsEarned = 0;
        int timeAdjustment = 0;
        
        if (isCorrect) {
            pointsEarned = gameMode.getPointsForDifficulty(questionDifficulty);
            timeAdjustment = gameMode.getTimeBonusForDifficulty(questionDifficulty);
        } else {
            timeAdjustment = -gameMode.getTimePenalty();
        }

        // Create user answer record
        UserAnswer userAnswer = UserAnswer.builder()
                .gameSession(session)
                .question(question)
                .selectedAnswer(selectedAnswer)
                .isCorrect(isCorrect)
                .pointsEarned(pointsEarned)
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .usedHint(request.getUsedHint())
                .build();
        userAnswerRepository.save(userAnswer);

        // Update session stats
        session.setTotalQuestions(session.getTotalQuestions() + 1); // Increment total questions answered
        if (isCorrect) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
            session.setTotalScore(session.getTotalScore() + pointsEarned);
        } else {
            session.setWrongAnswers(session.getWrongAnswers() + 1);
            session.setLivesRemaining(session.getLivesRemaining() - 1);
        }

        // Survival mode - game ends only when lives run out (timer is handled by frontend)
        List<UserAnswer> allAnswers = userAnswerRepository.findByGameSessionId(sessionId);
        int questionsAnswered = allAnswers.size();
        boolean isGameOver = session.getLivesRemaining() <= 0;

        if (isGameOver) {
            session.complete();
        }

        gameSessionRepository.save(session);

        // Determine new difficulty for next question based on updated score and game mode
        QuestionLevelType newDifficulty = determineCurrentDifficulty(session.getTotalScore(), gameMode);

        return QcmSubmitAnswerResponse.builder()
                .isCorrect(isCorrect)
                .explanation(question.getExplanation())
                .correctAnswerId(correctAnswer.getId())
                .correctAnswerContent(correctAnswer.getContent())
                .currentScore(session.getTotalScore())
                .correctAnswers(session.getCorrectAnswers())
                .wrongAnswers(session.getWrongAnswers())
                .livesRemaining(session.getLivesRemaining())
                .questionsAnswered(questionsAnswered)
                .isGameOver(isGameOver)
                .timerExpired(false) // Timer is managed by frontend
                .difficultyLevel(newDifficulty.name())
                .hasNextQuestion(!isGameOver)
                .nextQuestionIndex(isGameOver ? null : questionsAnswered + 1)
                .timeAdjustment(timeAdjustment) // Time bonus/penalty for frontend to apply
                .build();
    }

    @Override
    public QcmGameResultResponse getGameResults(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", sessionId));

        if (session.getCompletedAt() == null) {
            throw new BadRequestException("Game session is not yet completed");
        }

        List<UserAnswer> answers = userAnswerRepository.findByGameSessionId(sessionId);

        // Get game mode
        GameMode gameMode = session.getGameMode();
        if (gameMode == null) {
            gameMode = GameMode.CLASSIC; // Default fallback
        }

        // Build question reviews
        List<QcmGameResultResponse.QuestionReview> reviews = new ArrayList<>();
        int index = 1;
        for (UserAnswer ua : answers) {
            Answer correctAnswer = answerRepository.findByQuestionIdAndIsCorrectTrue(ua.getQuestion().getId()).stream()
                    .findFirst()
                    .orElse(null);

            reviews.add(QcmGameResultResponse.QuestionReview.builder()
                    .questionIndex(index++)
                    .questionContent(ua.getQuestion().getContent())
                    .difficultyLevel(ua.getQuestion().getLevel().getLevelName().name())
                    .userAnswerContent(ua.getSelectedAnswer() != null ? ua.getSelectedAnswer().getContent() : "No answer")
                    .correctAnswerContent(correctAnswer != null ? correctAnswer.getContent() : "N/A")
                    .wasCorrect(ua.isCorrect())
                    .explanation(ua.getQuestion().getExplanation())
                    .timeTakenSeconds(ua.getTimeTakenSeconds())
                    .pointsEarned(ua.getPointsEarned())
                    .build());
        }

        // Calculate duration
        long durationSeconds = Duration.between(session.getStartedAt(), session.getCompletedAt()).getSeconds();

        // Calculate efficiency (score per minute)
        double efficiency = gameMode.calculateEfficiency(session.getTotalScore(), (int) durationSeconds);

        // Determine performance level
        String performanceLevel = determinePerformanceLevel(session.getAccuracy());

        // Get starting and ending difficulty
        QuestionLevelType startingDifficulty = answers.isEmpty() ? QuestionLevelType.EASY : answers.get(0).getQuestion().getLevel().getLevelName();
        QuestionLevelType endingDifficulty = answers.isEmpty() ? QuestionLevelType.EASY : answers.get(answers.size() - 1).getQuestion().getLevel().getLevelName();

        // Find max difficulty reached during the game
        QuestionLevelType maxDifficultyReached = answers.stream()
                .map(ua -> ua.getQuestion().getLevel().getLevelName())
                .max((d1, d2) -> {
                    int order1 = getDifficultyOrder(d1);
                    int order2 = getDifficultyOrder(d2);
                    return Integer.compare(order1, order2);
                })
                .orElse(QuestionLevelType.EASY);

        // Determine end reason
        String endReason = session.getLivesRemaining() <= 0 ? "LIVES_DEPLETED" : "TIMER_EXPIRED";

        return QcmGameResultResponse.builder()
                .sessionId(session.getId())
                .categoryName(session.getCategory() != null ? session.getCategory().getName() : "Mixed")
                .gameMode(gameMode.name())
                .globalTimerDuration(session.getGlobalTimerDuration())
                .maxTimerDuration(session.getMaxTimerDuration())
                .totalScore(session.getTotalScore())
                .correctAnswers(session.getCorrectAnswers())
                .wrongAnswers(session.getWrongAnswers())
                .totalQuestionsAnswered(answers.size())
                .accuracy(session.getAccuracy())
                .efficiency(efficiency)
                .livesRemaining(session.getLivesRemaining())
                .startedAt(session.getStartedAt())
                .completedAt(session.getCompletedAt())
                .durationSeconds(durationSeconds)
                .endReason(endReason)
                .performanceLevel(performanceLevel)
                .pointsEarned(session.getTotalScore())
                .startingDifficulty(startingDifficulty.name())
                .endingDifficulty(endingDifficulty.name())
                .maxDifficultyReached(maxDifficultyReached.name())
                .questionReviews(reviews)
                .build();
    }

    /**
     * Get numeric order for difficulty level comparison.
     */
    private int getDifficultyOrder(QuestionLevelType difficulty) {
        return switch (difficulty) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
            case EXPERT -> 4;
        };
    }

    @Override
    public void abandonGameSession(Long sessionId) {
        GameSession session = getValidSession(sessionId);

        session.complete();
        gameSessionRepository.save(session);
    }

    @Override
    @Transactional(readOnly = true)
    public QcmGameSessionResponse getSessionState(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", sessionId));

        int currentQuestionIndex = (int) userAnswerRepository.countByGameSessionId(sessionId);

        // Get game mode
        GameMode gameMode = session.getGameMode();
        if (gameMode == null) {
            gameMode = GameMode.CLASSIC; // Default fallback
        }

        // Determine current difficulty based on score and game mode
        QuestionLevelType currentDifficulty = determineCurrentDifficulty(session.getTotalScore(), gameMode);

        return QcmGameSessionResponse.builder()
                .sessionId(session.getId())
                .categoryId(session.getCategory() != null ? session.getCategory().getId() : null)
                .categoryName(session.getCategory() != null ? session.getCategory().getName() : null)
                .livesRemaining(session.getLivesRemaining())
                .currentQuestionIndex(currentQuestionIndex)
                .globalTimerDuration(session.getGlobalTimerDuration())
                .maxTimerDuration(session.getMaxTimerDuration())
                .startedAt(session.getStartedAt())
                .status(session.getCompletedAt() != null ? "COMPLETED" : "IN_PROGRESS")
                .currentDifficulty(currentDifficulty.name())
                .gameMode(gameMode.name())
                .build();
    }

    // ==================== Private Helper Methods ====================

    private GameSession getValidSession(Long sessionId) {
        GameSession session = gameSessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", sessionId));

        if (session.getCompletedAt() != null) {
            throw new BadRequestException("Game session is already completed");
        }

        return session;
    }

    private Question selectNextQuestion(GameSession session, List<UserAnswer> answeredQuestions, QuestionLevelType difficulty) {
        // Get IDs of already answered questions
        Set<Long> answeredIds = answeredQuestions.stream()
                .map(ua -> ua.getQuestion().getId())
                .collect(Collectors.toSet());

        // Get questions for this session's category
        List<Question> availableQuestions;
        if (session.getGame() != null) {
            availableQuestions = questionRepository.findByCategoryIdAndGameIdAndStatus(
                    session.getCategory().getId(),
                    session.getGame().getId(),
                    QuestionStatusType.ACTIVE);
        } else {
            // Fallback: get questions by category only
            availableQuestions = questionRepository.findByCategoryIdAndStatus(
                    session.getCategory().getId(),
                    QuestionStatusType.ACTIVE);
        }

        // Filter out already answered questions
        availableQuestions = availableQuestions.stream()
                .filter(q -> !answeredIds.contains(q.getId()))
                .collect(Collectors.toList());

        // Try to get questions at current difficulty level
        List<Question> difficultyMatched = availableQuestions.stream()
                .filter(q -> q.getLevel().getLevelName() == difficulty)
                .collect(Collectors.toList());

        if (!difficultyMatched.isEmpty()) {
            Collections.shuffle(difficultyMatched);
            return difficultyMatched.get(0);
        }

        // Fallback: get any available question, preferring closer difficulty levels
        if (!availableQuestions.isEmpty()) {
            // Sort by difficulty proximity
            availableQuestions.sort(Comparator.comparingInt(q -> 
                    Math.abs(q.getLevel().getLevelName().ordinal() - difficulty.ordinal())));

            return availableQuestions.get(0);
        }

        throw new BadRequestException("No more questions available");
    }

    /**
     * Determine difficulty based on current score and game mode.
     * Everyone starts at EASY and progresses through score thresholds specific to each mode.
     */
    private QuestionLevelType determineCurrentDifficulty(int currentScore, GameMode gameMode) {
        return gameMode.determineDifficulty(currentScore);
    }

    private String determinePerformanceLevel(double accuracy) {
        if (accuracy >= 90) {
            return "Excellent";
        } else if (accuracy >= 75) {
            return "Good";
        } else if (accuracy >= 50) {
            return "Average";
        } else {
            return "Needs Improvement";
        }
    }

    @Override
    @Transactional(readOnly = true)
    public QcmUserStatsResponse getUserStats(Long userId) {
        // Get all completed game sessions for the user
        List<GameSession> sessions = gameSessionRepository.findByUserIdAndCompletedAtIsNotNull(userId);
        
        if (sessions.isEmpty()) {
            return QcmUserStatsResponse.builder()
                    .totalGamesPlayed(0)
                    .totalQuestionsAnswered(0)
                    .totalCorrectAnswers(0)
                    .totalWrongAnswers(0)
                    .overallAccuracy(0.0)
                    .totalPointsEarned(0)
                    .bestScore(0)
                    .averageScore(0.0)
                    .recentAccuracy(0.0)
                    .recentGamesPlayed(0)
                    .currentStreak(0)
                    .highestDifficultyReached("EASY")
                    .leaderboardPosition(0)
                    .totalPlayers(0)
                    .categoryStats(new ArrayList<>())
                    .gameModeStats(new ArrayList<>())
                    .recentGames(new ArrayList<>())
                    .build();
        }

        // Calculate core stats
        int totalGamesPlayed = sessions.size();
        int totalQuestionsAnswered = sessions.stream().mapToInt(GameSession::getTotalQuestions).sum();
        int totalCorrectAnswers = sessions.stream().mapToInt(GameSession::getCorrectAnswers).sum();
        int totalWrongAnswers = sessions.stream().mapToInt(GameSession::getWrongAnswers).sum();
        int totalPointsEarned = sessions.stream().mapToInt(GameSession::getTotalScore).sum();
        int bestScore = sessions.stream().mapToInt(GameSession::getTotalScore).max().orElse(0);
        double averageScore = (double) totalPointsEarned / totalGamesPlayed;
        
        // Use average of per-game accuracies (not total accuracy)
        double overallAccuracy = calculateAverageGameAccuracy(sessions);

        // Recent performance (last 10 games)
        List<GameSession> recentSessions = sessions.stream()
                .sorted((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()))
                .limit(10)
                .collect(Collectors.toList());
        
        // Recent accuracy is also average of per-game accuracies
        double recentAccuracy = calculateAverageGameAccuracy(recentSessions);

        // Calculate streak (simplified - count consecutive days with games)
        int currentStreak = calculateStreak(sessions);

        // Determine best performing level (where player has highest accuracy)
        String bestPerformingLevel = determineBestPerformingLevel(userId);
        Map<String, Double> accuracyByDifficulty = calculateAccuracyByDifficulty(userId);

        // Category breakdown
        Map<Long, List<GameSession>> byCategory = sessions.stream()
                .filter(s -> s.getCategory() != null)
                .collect(Collectors.groupingBy(s -> s.getCategory().getId()));
        
        List<QcmUserStatsResponse.CategoryStats> categoryStats = new ArrayList<>();
        for (Map.Entry<Long, List<GameSession>> entry : byCategory.entrySet()) {
            List<GameSession> catSessions = entry.getValue();
            Category category = catSessions.get(0).getCategory();
            int catQuestions = catSessions.stream().mapToInt(GameSession::getTotalQuestions).sum();
            int catCorrect = catSessions.stream().mapToInt(GameSession::getCorrectAnswers).sum();
            
            categoryStats.add(QcmUserStatsResponse.CategoryStats.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .gamesPlayed(catSessions.size())
                    .questionsAnswered(catQuestions)
                    .accuracy(catQuestions > 0 ? (double) catCorrect / catQuestions * 100 : 0.0)
                    .bestScore(catSessions.stream().mapToInt(GameSession::getTotalScore).max().orElse(0))
                    .averageScore(catSessions.stream().mapToInt(GameSession::getTotalScore).average().orElse(0))
                    .build());
        }

        // Recent games history
        List<QcmUserStatsResponse.RecentGame> recentGames = recentSessions.stream()
                .limit(5)
                .map(s -> QcmUserStatsResponse.RecentGame.builder()
                        .sessionId(s.getId())
                        .categoryName(s.getCategory() != null ? s.getCategory().getName() : "Mixed")
                        .score(s.getTotalScore())
                        .correctAnswers(s.getCorrectAnswers())
                        .totalQuestions(s.getTotalQuestions())
                        .accuracy(s.getAccuracy())
                        .difficultyReached(getEndingDifficulty(s.getId()))
                        .completedAt(s.getCompletedAt().toString())
                        .durationSeconds(s.getCompletedAt() != null && s.getStartedAt() != null
                                ? (int) Duration.between(s.getStartedAt(), s.getCompletedAt()).getSeconds()
                                : 0)
                        .build())
                .collect(Collectors.toList());

        // Game mode breakdown
        List<QcmUserStatsResponse.GameModeStats> gameModeStats = new ArrayList<>();
        for (GameMode mode : GameMode.values()) {
            List<GameSession> modeSessions = sessions.stream()
                    .filter(s -> s.getGameMode() == mode)
                    .collect(Collectors.toList());
            
            if (!modeSessions.isEmpty()) {
                int modeGames = modeSessions.size();
                int modeScore = modeSessions.stream().mapToInt(GameSession::getTotalScore).sum();
                int modeQuestions = modeSessions.stream().mapToInt(GameSession::getTotalQuestions).sum();
                int modeCorrect = modeSessions.stream().mapToInt(GameSession::getCorrectAnswers).sum();
                
                gameModeStats.add(QcmUserStatsResponse.GameModeStats.builder()
                        .gameMode(mode.name())
                        .gamesPlayed(modeGames)
                        .totalScore(modeScore)
                        .averageScore((double) modeScore / modeGames)
                        .bestScore(modeSessions.stream().mapToInt(GameSession::getTotalScore).max().orElse(0))
                        .accuracy(modeQuestions > 0 ? (double) modeCorrect / modeQuestions * 100 : 0.0)
                        .totalQuestions(modeQuestions)
                        .correctAnswers(modeCorrect)
                        .build());
            }
        }

        // Leaderboard position (simplified calculation)
        int leaderboardPosition = calculateLeaderboardPosition(userId);
        int totalPlayers = gameSessionRepository.countDistinctUsersWithCompletedSessions();

        return QcmUserStatsResponse.builder()
                .totalGamesPlayed(totalGamesPlayed)
                .totalQuestionsAnswered(totalQuestionsAnswered)
                .totalCorrectAnswers(totalCorrectAnswers)
                .totalWrongAnswers(totalWrongAnswers)
                .overallAccuracy(overallAccuracy)
                .totalPointsEarned(totalPointsEarned)
                .bestScore(bestScore)
                .averageScore(averageScore)
                .recentAccuracy(recentAccuracy)
                .recentGamesPlayed(recentSessions.size())
                .currentStreak(currentStreak)
                .bestPerformingLevel(bestPerformingLevel)
                .accuracyByDifficulty(accuracyByDifficulty)
                .categoryStats(categoryStats)
                .gameModeStats(gameModeStats)
                .recentGames(recentGames)
                .leaderboardPosition(leaderboardPosition)
                .totalPlayers(totalPlayers)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QcmLeaderboardResponse getLeaderboard(int page, int size, Long categoryId, String gameMode) {
        // Get all completed sessions, optionally filtered by category and game mode
        List<GameSession> sessions;
        boolean filterByGameMode = gameMode != null && !"ALL".equals(gameMode);
        
        if (categoryId != null && filterByGameMode) {
            GameMode mode = GameMode.valueOf(gameMode);
            sessions = gameSessionRepository.findByCategoryIdAndGameModeAndCompletedAtIsNotNull(categoryId, mode);
        } else if (categoryId != null) {
            sessions = gameSessionRepository.findByCategoryIdAndCompletedAtIsNotNull(categoryId);
        } else if (filterByGameMode) {
            GameMode mode = GameMode.valueOf(gameMode);
            sessions = gameSessionRepository.findByGameModeAndCompletedAtIsNotNull(mode);
        } else {
            sessions = gameSessionRepository.findByCompletedAtIsNotNull();
        }

        // Aggregate by user
        Map<Long, List<GameSession>> byUser = sessions.stream()
                .collect(Collectors.groupingBy(s -> s.getUser().getId()));

        // Calculate leaderboard entries
        List<QcmLeaderboardResponse.LeaderboardEntry> entries = new ArrayList<>();
        for (Map.Entry<Long, List<GameSession>> entry : byUser.entrySet()) {
            Long userId = entry.getKey();
            List<GameSession> userSessions = entry.getValue();
            User user = userSessions.get(0).getUser();
            
            int totalScore = userSessions.stream().mapToInt(GameSession::getTotalScore).sum();
            int gamesPlayed = userSessions.size();
            int totalQuestions = userSessions.stream().mapToInt(GameSession::getTotalQuestions).sum();
            int totalCorrect = userSessions.stream().mapToInt(GameSession::getCorrectAnswers).sum();
            double accuracy = totalQuestions > 0 ? (double) totalCorrect / totalQuestions * 100 : 0.0;
            int bestScore = userSessions.stream().mapToInt(GameSession::getTotalScore).max().orElse(0);
            double averageScore = (double) totalScore / gamesPlayed;

            // Find top category
            String topCategory = userSessions.stream()
                    .filter(s -> s.getCategory() != null)
                    .collect(Collectors.groupingBy(s -> s.getCategory().getName(), Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");

            // Calculate difficulty factor (1-4)
            String highestDifficulty = determineHighestDifficultyReached(userId);
            int difficultyFactor = getDifficultyFactor(highestDifficulty);

            // Calculate composite score (0-100) for objective ranking
            // Formula: (AvgScore normalized) * 0.5 + Accuracy * 0.3 + DifficultyFactor * 5
            // Assuming max average score could be around 100 points per game
            double normalizedAvgScore = Math.min(averageScore / 100.0 * 100, 100); // Normalize to 0-100
            double compositeScore = (normalizedAvgScore * 0.5) + (accuracy * 0.3) + (difficultyFactor * 5);
            compositeScore = Math.min(compositeScore, 100); // Cap at 100

            // Calculate game mode breakdown
            int blitzGamesPlayed = (int) userSessions.stream()
                    .filter(s -> s.getGameMode() == GameMode.BLITZ).count();
            int rushGamesPlayed = (int) userSessions.stream()
                    .filter(s -> s.getGameMode() == GameMode.RUSH).count();
            int classicGamesPlayed = (int) userSessions.stream()
                    .filter(s -> s.getGameMode() == GameMode.CLASSIC).count();

            entries.add(QcmLeaderboardResponse.LeaderboardEntry.builder()
                    .userId(userId)
                    .username(user.getUsername())
                    .avatarUrl(null) // Add avatar support if needed
                    .compositeScore(compositeScore)
                    .totalScore(totalScore)
                    .gamesPlayed(gamesPlayed)
                    .accuracy(accuracy)
                    .bestScore(bestScore)
                    .averageScore(averageScore)
                    .topCategory(topCategory)
                    .highestDifficulty(highestDifficulty)
                    .difficultyFactor(difficultyFactor)
                    .blitzGamesPlayed(blitzGamesPlayed)
                    .rushGamesPlayed(rushGamesPlayed)
                    .classicGamesPlayed(classicGamesPlayed)
                    .build());
        }

        // Sort by composite score descending (objective ranking)
        entries.sort((a, b) -> Double.compare(b.getCompositeScore(), a.getCompositeScore()));

        // Assign ranks
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setRank(i + 1);
        }

        // Paginate
        int totalPlayers = entries.size();
        int totalPages = (int) Math.ceil((double) totalPlayers / size);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, entries.size());
        
        List<QcmLeaderboardResponse.LeaderboardEntry> pagedEntries = 
                fromIndex < entries.size() ? entries.subList(fromIndex, toIndex) : new ArrayList<>();

        return QcmLeaderboardResponse.builder()
                .gameMode(gameMode != null ? gameMode : "ALL")
                .entries(pagedEntries)
                .totalPlayers(totalPlayers)
                .currentPage(page)
                .totalPages(totalPages)
                .build();
    }

    // Helper method to convert difficulty string to numeric factor
    private int getDifficultyFactor(String difficulty) {
        if (difficulty == null) return 1;
        switch (difficulty) {
            case "EXPERT": return 4;
            case "HARD": return 3;
            case "MEDIUM": return 2;
            case "EASY": 
            default: return 1;
        }
    }

    // Helper methods for stats
    private int calculateStreak(List<GameSession> sessions) {
        if (sessions.isEmpty()) return 0;
        
        // Sort by completed date descending
        List<LocalDateTime> dates = sessions.stream()
                .map(GameSession::getCompletedAt)
                .filter(java.util.Objects::nonNull)
                .sorted((a, b) -> b.compareTo(a))
                .collect(Collectors.toList());
        
        if (dates.isEmpty()) return 0;
        
        int streak = 1;
        LocalDateTime lastDate = dates.get(0).toLocalDate().atStartOfDay();
        
        for (int i = 1; i < dates.size(); i++) {
            LocalDateTime currentDate = dates.get(i).toLocalDate().atStartOfDay();
            long daysBetween = Duration.between(currentDate, lastDate).toDays();
            
            if (daysBetween == 1) {
                streak++;
                lastDate = currentDate;
            } else if (daysBetween > 1) {
                break;
            }
        }
        
        return streak;
    }

    private String determineHighestDifficultyReached(Long userId) {
        List<UserAnswer> answers = userAnswerRepository.findByGameSessionUserId(userId);
        
        if (answers.isEmpty()) return "EASY";
        
        boolean hasExpert = answers.stream()
                .anyMatch(a -> a.getQuestion().getLevel().getLevelName() == QuestionLevelType.EXPERT);
        boolean hasHard = answers.stream()
                .anyMatch(a -> a.getQuestion().getLevel().getLevelName() == QuestionLevelType.HARD);
        boolean hasMedium = answers.stream()
                .anyMatch(a -> a.getQuestion().getLevel().getLevelName() == QuestionLevelType.MEDIUM);
        
        if (hasExpert) return "EXPERT";
        if (hasHard) return "HARD";
        if (hasMedium) return "MEDIUM";
        return "EASY";
    }

    /**
     * Calculate the difficulty level where the player performs best.
     * This is based on accuracy per difficulty level, not just reaching the level.
     */
    private String determineBestPerformingLevel(Long userId) {
        List<UserAnswer> answers = userAnswerRepository.findByGameSessionUserId(userId);
        
        if (answers.isEmpty()) return "N/A";
        
        // Group answers by difficulty and calculate accuracy for each
        Map<QuestionLevelType, List<UserAnswer>> byDifficulty = answers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getLevel().getLevelName()));
        
        double bestAccuracy = -1;
        QuestionLevelType bestLevel = QuestionLevelType.EASY;
        
        for (Map.Entry<QuestionLevelType, List<UserAnswer>> entry : byDifficulty.entrySet()) {
            List<UserAnswer> levelAnswers = entry.getValue();
            long correct = levelAnswers.stream().filter(UserAnswer::isCorrect).count();
            double accuracy = (double) correct / levelAnswers.size() * 100;
            
            // Only consider levels with at least 3 questions answered for statistical relevance
            if (levelAnswers.size() >= 3 && accuracy > bestAccuracy) {
                bestAccuracy = accuracy;
                bestLevel = entry.getKey();
            }
        }
        
        return bestLevel.name();
    }

    /**
     * Calculate accuracy by difficulty level.
     */
    private Map<String, Double> calculateAccuracyByDifficulty(Long userId) {
        List<UserAnswer> answers = userAnswerRepository.findByGameSessionUserId(userId);
        
        Map<String, Double> accuracyByDifficulty = new java.util.HashMap<>();
        
        if (answers.isEmpty()) {
            accuracyByDifficulty.put("EASY", 0.0);
            accuracyByDifficulty.put("MEDIUM", 0.0);
            accuracyByDifficulty.put("HARD", 0.0);
            accuracyByDifficulty.put("EXPERT", 0.0);
            return accuracyByDifficulty;
        }
        
        Map<QuestionLevelType, List<UserAnswer>> byDifficulty = answers.stream()
                .collect(Collectors.groupingBy(a -> a.getQuestion().getLevel().getLevelName()));
        
        for (QuestionLevelType level : QuestionLevelType.values()) {
            List<UserAnswer> levelAnswers = byDifficulty.getOrDefault(level, new ArrayList<>());
            if (levelAnswers.isEmpty()) {
                accuracyByDifficulty.put(level.name(), 0.0);
            } else {
                long correct = levelAnswers.stream().filter(UserAnswer::isCorrect).count();
                double accuracy = (double) correct / levelAnswers.size() * 100;
                accuracyByDifficulty.put(level.name(), accuracy);
            }
        }
        
        return accuracyByDifficulty;
    }

    /**
     * Calculate average accuracy from per-game accuracies.
     * This gives equal weight to each game regardless of question count.
     */
    private double calculateAverageGameAccuracy(List<GameSession> sessions) {
        if (sessions.isEmpty()) return 0.0;
        
        double totalAccuracy = sessions.stream()
                .mapToDouble(GameSession::getAccuracy)
                .sum();
        
        return totalAccuracy / sessions.size();
    }

    private String getEndingDifficulty(Long sessionId) {
        List<UserAnswer> answers = userAnswerRepository.findByGameSessionId(sessionId);
        if (answers.isEmpty()) return "EASY";
        return answers.get(answers.size() - 1).getQuestion().getLevel().getLevelName().name();
    }

    private int calculateLeaderboardPosition(Long userId) {
        // Get all users' total scores
        List<GameSession> allSessions = gameSessionRepository.findByCompletedAtIsNotNull();
        Map<Long, Integer> userTotalScores = allSessions.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getUser().getId(),
                        Collectors.summingInt(GameSession::getTotalScore)));
        
        // Sort by score descending
        List<Map.Entry<Long, Integer>> sorted = userTotalScores.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());
        
        // Find user's position
        for (int i = 0; i < sorted.size(); i++) {
            if (sorted.get(i).getKey().equals(userId)) {
                return i + 1;
            }
        }
        
        return 0;
    }
}
