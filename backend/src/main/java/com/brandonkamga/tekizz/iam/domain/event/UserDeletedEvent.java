package com.brandonkamga.tekizz.iam.domain.event;

import com.brandonkamga.tekizz.iam.domain.model.vo.UserId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class UserDeletedEvent extends DomainEvent {

    private final UserId userId;

    public UserDeletedEvent(UserId userId) {
        this.userId = userId;
    }

    public UserId getUserId() { return userId; }
}
