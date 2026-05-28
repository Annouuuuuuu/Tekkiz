package com.brandonkamga.tekizz.gaming.smatch.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record SmatchDeckId(Long value) implements ValueObject {
    public SmatchDeckId { Objects.requireNonNull(value, "SmatchDeckId must not be null"); }
    public static SmatchDeckId of(Long value) { return new SmatchDeckId(value); }
}
