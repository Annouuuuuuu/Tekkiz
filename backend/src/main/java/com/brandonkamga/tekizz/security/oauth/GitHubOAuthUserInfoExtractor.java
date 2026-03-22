package com.brandonkamga.tekizz.security.oauth;

import com.brandonkamga.tekizz.domain.Profile;
import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * GitHub OAuth2 user info extractor implementation.
 * Extracts user information specific to GitHub's OAuth2 response format.
 */
@Component
public class GitHubOAuthUserInfoExtractor implements OAuthUserInfoExtractor {

    @Override
    public ProviderType getProviderType() {
        return ProviderType.GITHUB;
    }

    @Override
    public boolean supports(String provider) {
        return ProviderType.GITHUB.name().equalsIgnoreCase(provider);
    }

    @Override
    public String extractEmail(OAuth2User oauthUser) {
        return oauthUser.getAttribute("email");
    }

    @Override
    public String extractName(OAuth2User oauthUser) {
        return oauthUser.getAttribute("name");
    }

    @Override
    public String extractProviderUserId(OAuth2User oauthUser) {
        // Handle GitHub's user ID which can be Integer or Long
        Map<String, Object> attributes = oauthUser.getAttributes();
        Object id = attributes.get("id");
        if (id == null) {
            return null;
        }
        return id.toString();
    }

    @Override
    public String extractUsername(OAuth2User oauthUser) {
        return oauthUser.getAttribute("login");
    }

    @Override
    public String extractFirstName(OAuth2User oauthUser) {
        return extractFirstName(extractName(oauthUser));
    }

    @Override
    public String extractLastName(OAuth2User oauthUser) {
        return extractLastName(extractName(oauthUser));
    }

    private String extractFirstName(String name) {
        if (name == null) {
            return null;
        }
        String[] parts = name.split(" ");
        return parts.length > 0 ? parts[0] : name;
    }

    private String extractLastName(String name) {
        if (name == null) {
            return null;
        }
        String[] parts = name.split(" ");
        return parts.length > 1 ? parts[1] : null;
    }

    @Override
    public User buildUser(OAuth2User oauthUser, Role role, Provider provider) {

        String email = extractEmail(oauthUser);
        String providerUserId = extractProviderUserId(oauthUser);
        String username = extractUsername(oauthUser);
        String name = extractName(oauthUser);

        if (providerUserId == null) {
            throw new IllegalStateException("GitHub ID not found");
        }

        if (email == null) {
            email = providerUserId + "@github.local";
        }

        if (username == null) {
            username = email.split("@")[0];
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setProvider(provider);
        user.setProviderUserId(providerUserId);
        user.setRole(role);

        // Create profile with firstName and lastName
        Profile profile = Profile.builder()
                .user(user)
                .firstName(extractFirstName(name))
                .lastName(extractLastName(name))
                .build();
        user.setProfile(profile);

        return user;
    }

}
