package com.brandonkamga.tekizz.contribution.domain.event;

import com.brandonkamga.tekizz.contribution.domain.model.vo.ContributionId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class ContributionSubmittedEvent extends DomainEvent {
    private final ContributionId contributionId;

    public ContributionSubmittedEvent(ContributionId contributionId) {
        this.contributionId = contributionId;
    }

    public ContributionId getContributionId() { return contributionId; }
}
