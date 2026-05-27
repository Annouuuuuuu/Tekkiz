package com.brandonkamga.tekizz.gaming.domain.event;

import com.brandonkamga.tekizz.gaming.domain.model.qcm.vo.SessionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class QcmSessionAbandonedEvent extends DomainEvent {
    private final SessionId sessionId;
    private final Long userId;

    public QcmSessionAbandonedEvent(SessionId sessionId, Long userId) {
        this.sessionId = sessionId;
        this.userId = userId;
    }

    public SessionId getSessionId() { return sessionId; }
    public Long getUserId() { return userId; }
}
