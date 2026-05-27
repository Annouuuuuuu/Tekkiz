package com.brandonkamga.tekizz.catalog.domain.event;

import com.brandonkamga.tekizz.catalog.domain.model.vo.QuestionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class QuestionCreatedEvent extends DomainEvent {
    private final QuestionId questionId;

    public QuestionCreatedEvent(QuestionId questionId) {
        this.questionId = questionId;
    }

    public QuestionId getQuestionId() { return questionId; }
}
