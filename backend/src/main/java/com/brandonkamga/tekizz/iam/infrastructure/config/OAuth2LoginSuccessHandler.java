package com.brandonkamga.tekizz.iam.infrastructure.config;

import com.brandonkamga.tekizz.iam.infrastructure.security.jwt.JwtService;
import com.brandonkamga.tekizz.iam.infrastructure.security.oauth.OAuthUserInfoExtractorFactory;
import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.iam.infrastructure.security.oauth.OAuthUserInfoExtractor;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.repository.RoleRepository;
import com.brandonkamga.tekizz.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Handles successful OAuth2 authentication.
 * Moved from config.OAuth2LoginSuccessHandler.
 * Uses UserRepository directly (legacy) until gaming context is migrated.
 */
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final OAuthUserInfoExtractorFactory extractorFactory;
    private final JwtService jwtService;
    private final String frontendUrl;

    public OAuth2LoginSuccessHandler(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ProviderRepository providerRepository,
            OAuthUserInfoExtractorFactory extractorFactory,
            JwtService jwtService,
            @Value("${app.frontend.url}") String frontendUrl) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.extractorFactory = extractorFactory;
        this.jwtService = jwtService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {

        OAuth2User oauthUser = extractOAuthUser(authentication);
        String provider = extractProvider(authentication);

        processOAuthPostLogin(oauthUser, provider);

        String token = jwtService.generateToken(authentication);
        sendRedirect(response, token);
    }

    private void processOAuthPostLogin(OAuth2User oauthUser, String provider) {
        OAuthUserInfoExtractor extractor = extractorFactory.getExtractor(provider);

        String email = extractor.extractEmail(oauthUser);
        if (email == null) {
            throw new RuntimeException("Email not provided by " + provider);
        }

        ProviderType providerType = extractor.getProviderType();
        Provider providerEntity = providerRepository.findByProviderName(providerType)
                .orElseGet(() -> providerRepository.save(Provider.builder()
                        .providerName(providerType)
                        .description(providerType.name() + " OAuth authentication")
                        .build()));

        userRepository.findByEmail(email)
                .map(existingUser -> {
                    updateOAuthUserFromProvider(existingUser, oauthUser, extractor);
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    Role userRole = roleRepository.findByRoleName(RoleType.USER)
                            .orElseThrow(() -> new RuntimeException("USER ROLE not found"));
                    User newUser = extractor.buildUser(oauthUser, userRole, providerEntity);
                    return userRepository.save(newUser);
                });
    }

    private void updateOAuthUserFromProvider(User existingUser, OAuth2User oauthUser,
                                             OAuthUserInfoExtractor extractor) {
        String firstName = extractor.extractFirstName(oauthUser);
        String lastName = extractor.extractLastName(oauthUser);
        String username = extractor.extractUsername(oauthUser);

        com.brandonkamga.tekizz.domain.Profile profile = existingUser.getProfile();
        if (profile == null) {
            profile = com.brandonkamga.tekizz.domain.Profile.builder()
                    .user(existingUser)
                    .build();
            existingUser.setProfile(profile);
        }

        if (firstName != null && !firstName.isEmpty()) {
            profile.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            profile.setLastName(lastName);
        }
        if (username != null && !username.isEmpty()) {
            existingUser.setUsername(username);
        }
    }

    protected OAuth2User extractOAuthUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof OAuth2User oauthUser) {
            return oauthUser;
        }
        throw new IllegalStateException("Principal is not OAuth2User");
    }

    protected String extractProvider(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            return oauthToken.getAuthorizedClientRegistrationId();
        }
        return authentication.getName();
    }

    protected void sendRedirect(HttpServletResponse response, String token) {
        try {
            response.sendRedirect(frontendUrl + "/oauth/callback?token=" + token);
        } catch (Exception e) {
            throw new RuntimeException("Failed to redirect after OAuth login", e);
        }
    }
}
