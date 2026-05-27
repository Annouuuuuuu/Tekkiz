package com.brandonkamga.tekizz.gaming.qcm.domain.event;

import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.QcmQuestionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class QcmQuestionCreatedEvent extends DomainEvent {
    private final QcmQuestionId questionId;

    public QcmQuestionCreatedEvent(QcmQuestionId questionId) {
        this.questionId = questionId;
    }

    public QcmQuestionId getQuestionId() { return questionId; }
}
