package com.brandonkamga.tekizz.gaming.qcm.domain.event;

import com.brandonkamga.tekizz.gaming.qcm.domain.model.vo.QcmQuestionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class QcmQuestionPublishedEvent extends DomainEvent {
    private final QcmQuestionId questionId;

    public QcmQuestionPublishedEvent(QcmQuestionId questionId) {
        this.questionId = questionId;
    }

    public QcmQuestionId getQuestionId() { return questionId; }
}
