package com.brandonkamga.tekizz.gaming.qcm.domain.event;

import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.SessionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class QcmSessionCompletedEvent extends DomainEvent {
    private final SessionId sessionId;
    private final Long userId;
    private final int finalScore;

    public QcmSessionCompletedEvent(SessionId sessionId, Long userId, int finalScore) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.finalScore = finalScore;
    }

    public SessionId getSessionId() { return sessionId; }
    public Long getUserId() { return userId; }
    public int getFinalScore() { return finalScore; }
}
