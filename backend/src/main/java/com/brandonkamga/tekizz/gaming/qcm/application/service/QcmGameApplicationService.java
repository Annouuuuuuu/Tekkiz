package com.brandonkamga.tekizz.gaming.qcm.application.service;

import com.brandonkamga.tekizz.dto.qcm.*;
import com.brandonkamga.tekizz.gaming.qcm.application.port.in.*;
import com.brandonkamga.tekizz.service.interfaces.QcmGameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gaming bounded context application service for QCM game operations.
 * Implements all use case ports and delegates to the existing QcmGameService.
 * This is the application layer orchestrator — business rules remain in the service.
 */
@Service
@Transactional
public class QcmGameApplicationService
        implements StartQcmSessionUseCase, GetNextQuestionUseCase, SubmitAnswerUseCase,
                   AbandonSessionUseCase, GetSessionResultUseCase, GetLeaderboardUseCase,
                   GetUserStatsUseCase {

    private final QcmGameService qcmGameService;

    public QcmGameApplicationService(QcmGameService qcmGameService) {
        this.qcmGameService = qcmGameService;
    }

    @Override
    public QcmGameSessionResponse start(StartSessionCommand command) {
        QcmGameConfigRequest config = new QcmGameConfigRequest();
        config.setCategoryId(command.categoryId());
        config.setTagIds(command.tagIds());
        config.setGameMode(command.gameMode());
        config.setLives(command.lives());
        return qcmGameService.createGameSession(command.userId(), config);
    }

    @Override
    @Transactional(readOnly = true)
    public QcmQuestionResponse getNext(Long sessionId) {
        return qcmGameService.getNextQuestion(sessionId);
    }

    @Override
    public QcmSubmitAnswerResponse submit(Long sessionId, QcmSubmitAnswerRequest request) {
        return qcmGameService.submitAnswer(sessionId, request);
    }

    @Override
    public void abandon(Long sessionId) {
        qcmGameService.abandonGameSession(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public QcmGameResultResponse getResult(Long sessionId) {
        return qcmGameService.getGameResults(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public QcmLeaderboardResponse getLeaderboard(int page, int size, Long categoryId, String gameMode) {
        return qcmGameService.getLeaderboard(page, size, categoryId, gameMode);
    }

    @Override
    @Transactional(readOnly = true)
    public QcmUserStatsResponse getStats(Long userId) {
        return qcmGameService.getUserStats(userId);
    }
}
