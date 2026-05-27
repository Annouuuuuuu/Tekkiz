package com.brandonkamga.tekizz.iam.domain.event;

import com.brandonkamga.tekizz.iam.domain.model.vo.UserId;
import com.brandonkamga.tekizz.shared.domain.DomainEvent;

public class UserRegisteredEvent extends DomainEvent {

    private final UserId userId;
    private final String email;
    private final String username;

    public UserRegisteredEvent(UserId userId, String email, String username) {
        this.userId = userId;
        this.email = email;
        this.username = username;
    }

    public UserId getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
}
