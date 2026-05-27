package com.brandonkamga.tekizz.contribution.domain.model.vo;

import com.brandonkamga.tekizz.shared.domain.ValueObject;
import java.util.Objects;

public record ContributionId(Long value) implements ValueObject {
    public ContributionId { Objects.requireNonNull(value, "ContributionId must not be null"); }
    public static ContributionId of(Long value) { return new ContributionId(value); }
}
