package com.brandonkamga.tekizz.contribution.domain.event;

import com.brandonkamga.tekizz.contribution.domain.model.vo.ContributionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class ContributionRejectedEvent extends DomainEvent {
    private final ContributionId contributionId;

    public ContributionRejectedEvent(ContributionId contributionId) {
        this.contributionId = contributionId;
    }

    public ContributionId getContributionId() { return contributionId; }
}
