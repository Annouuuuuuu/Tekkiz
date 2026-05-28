package com.brandonkamga.tekizz.gaming.qcm.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record QcmQuestionId(Long value) implements ValueObject {
    public QcmQuestionId { Objects.requireNonNull(value, "QcmQuestionId must not be null"); }
    public static QcmQuestionId of(Long value) { return new QcmQuestionId(value); }
}
