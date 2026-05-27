package com.brandonkamga.tekizz.catalog.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record QuestionId(Long value) implements ValueObject {
    public QuestionId { Objects.requireNonNull(value, "QuestionId must not be null"); }
    public static QuestionId of(Long value) { return new QuestionId(value); }
}
