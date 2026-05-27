package com.brandonkamga.tekizz.iam.infrastructure.security.oauth;

import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Strategy interface for extracting user information from OAuth2 providers.
 * Moved from security.oauth to iam.infrastructure.security.oauth.
 */
public interface OAuthUserInfoExtractor {

    ProviderType getProviderType();

    boolean supports(String provider);

    String extractEmail(OAuth2User oauthUser);

    String extractName(OAuth2User oauthUser);

    String extractProviderUserId(OAuth2User oauthUser);

    String extractUsername(OAuth2User oauthUser);

    User buildUser(OAuth2User oauthUser, Role role, Provider provider);

    default String extractFirstName(OAuth2User oauthUser) {
        return null;
    }

    default String extractLastName(OAuth2User oauthUser) {
        return null;
    }
}
