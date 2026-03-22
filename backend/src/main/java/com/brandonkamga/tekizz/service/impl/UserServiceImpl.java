package com.brandonkamga.tekizz.service.impl;

import com.brandonkamga.tekizz.domain.Profile;
import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.repository.RoleRepository;
import com.brandonkamga.tekizz.repository.UserRepository;
import com.brandonkamga.tekizz.security.oauth.OAuthUserInfoExtractor;
import com.brandonkamga.tekizz.security.oauth.OAuthUserInfoExtractorFactory;
import com.brandonkamga.tekizz.service.interfaces.UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of UserService.
 * Follows Single Responsibility Principle.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final OAuthUserInfoExtractorFactory extractorFactory;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            ProviderRepository providerRepository,
            OAuthUserInfoExtractorFactory extractorFactory) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.extractorFactory = extractorFactory;
    }

    /**
     * Process OAuth2 login by extracting user info and creating/finding user.
     * Uses Strategy Pattern via OAuthUserInfoExtractor.
     * 
     * @param oauthUser the OAuth2 user from the provider
     * @param provider the OAuth provider name (google, github, etc.)
     * @return the existing or newly created User
     */
    @Override
    public User processOAuthPostLogin(OAuth2User oauthUser, String provider) {
        OAuthUserInfoExtractor extractor = extractorFactory.getExtractor(provider);
        
        String email = extractor.extractEmail(oauthUser);
        if (email == null) {
            throw new RuntimeException("Email not provided by " + provider);
        }

        // Get or create provider
        ProviderType providerType = extractor.getProviderType();
        Provider providerEntity = providerRepository.findByProviderName(providerType)
                .orElseGet(() -> providerRepository.save(Provider.builder()
                        .providerName(providerType)
                        .description(providerType.name() + " OAuth authentication")
                        .build()));

        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // Update existing user's profile data from provider
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

    /**
     * Update existing OAuth user's profile data from the provider.
     * This ensures user info stays synced with the OAuth provider.
     * 
     * @param existingUser the existing user in the database
     * @param oauthUser the OAuth2 user from the provider
     * @param extractor the OAuth user info extractor
     */
    private void updateOAuthUserFromProvider(User existingUser, OAuth2User oauthUser, OAuthUserInfoExtractor extractor) {
        String firstName = extractor.extractFirstName(oauthUser);
        String lastName = extractor.extractLastName(oauthUser);
        String username = extractor.extractUsername(oauthUser);
        
        // Get or create profile
        Profile profile = existingUser.getProfile();
        if (profile == null) {
            profile = Profile.builder()
                    .user(existingUser)
                    .build();
            existingUser.setProfile(profile);
        }
        
        // Update fields only if they are not null/empty from the provider
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

    @Override
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByIdOptional(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmailExcept(Long id, String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && !user.get().getId().equals(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsernameExcept(Long id, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent() && !user.get().getId().equals(id);
    }
}
