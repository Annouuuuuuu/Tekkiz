package com.brandonkamga.tekizz.catalog.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record TagId(Long value) implements ValueObject {
    public TagId { Objects.requireNonNull(value, "TagId must not be null"); }
    public static TagId of(Long value) { return new TagId(value); }
}
