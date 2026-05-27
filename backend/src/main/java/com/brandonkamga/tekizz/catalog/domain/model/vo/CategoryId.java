package com.brandonkamga.tekizz.catalog.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record CategoryId(Long value) implements ValueObject {
    public CategoryId { Objects.requireNonNull(value, "CategoryId must not be null"); }
    public static CategoryId of(Long value) { return new CategoryId(value); }
}
