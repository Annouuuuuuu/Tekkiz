package com.brandonkamga.tekizz.gaming.qcm.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record SessionId(Long value) implements ValueObject {
    public SessionId { Objects.requireNonNull(value, "SessionId must not be null"); }
    public static SessionId of(Long value) { return new SessionId(value); }
}
