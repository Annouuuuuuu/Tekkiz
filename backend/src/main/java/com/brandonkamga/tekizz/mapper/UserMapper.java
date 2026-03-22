package com.brandonkamga.tekizz.mapper;

import com.brandonkamga.tekizz.domain.Profile;
import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.UserRequest;
import com.brandonkamga.tekizz.dto.UserResponse;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.repository.RoleRepository;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;

    public UserMapper(RoleRepository roleRepository, ProviderRepository providerRepository) {
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
    }

    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .providerName(user.getProvider() != null ? user.getProvider().getProviderName().name() : null)
                .providerUserId(user.getProviderUserId())
                .roleName(user.getRole() != null ? user.getRole().getRoleName().name() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt());

        // Map profile fields if profile exists
        Profile profile = user.getProfile();
        if (profile != null) {
            builder.firstName(profile.getFirstName())
                    .lastName(profile.getLastName())
                    .avatarUrl(profile.getAvatarUrl())
                    .country(profile.getCountry())
                    .bio(profile.getBio());
        }

        return builder.build();
    }

    public User toEntity(UserRequest request) {
        if (request == null) {
            return null;
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        if (request.getProviderName() != null) {
            Provider provider = providerRepository.findByProviderName(request.getProviderName())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider", "name", request.getProviderName()));
            user.setProvider(provider);
        }
        
        user.setProviderUserId(request.getProviderUserId());

        if (request.getRoleName() != null) {
            try {
                RoleType roleType = RoleType.valueOf(request.getRoleName());
                user.setRole(roleRepository.findByRoleName(roleType)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName())));
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Role", "name", request.getRoleName());
            }
        }

        // Create profile if any profile field is provided
        if (hasProfileFields(request)) {
            Profile profile = createProfileFromRequest(request, user);
            user.setProfile(profile);
        }

        return user;
    }

    public User updateEntity(User user, UserRequest request) {
        if (request == null || user == null) {
            return user;
        }

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProviderName() != null) {
            Provider provider = providerRepository.findByProviderName(request.getProviderName())
                    .orElseThrow(() -> new ResourceNotFoundException("Provider", "name", request.getProviderName()));
            user.setProvider(provider);
        }
        if (request.getProviderUserId() != null) {
            user.setProviderUserId(request.getProviderUserId());
        }
        if (request.getRoleName() != null) {
            try {
                RoleType roleType = RoleType.valueOf(request.getRoleName());
                user.setRole(roleRepository.findByRoleName(roleType)
                        .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName())));
            } catch (IllegalArgumentException e) {
                throw new ResourceNotFoundException("Role", "name", request.getRoleName());
            }
        }

        // Update or create profile
        updateProfileFromRequest(user, request);

        return user;
    }

    private boolean hasProfileFields(UserRequest request) {
        return request.getFirstName() != null ||
                request.getLastName() != null ||
                request.getAvatarUrl() != null ||
                request.getCountry() != null ||
                request.getBio() != null;
    }

    private Profile createProfileFromRequest(UserRequest request, User user) {
        return Profile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .avatarUrl(request.getAvatarUrl())
                .country(request.getCountry())
                .bio(request.getBio())
                .build();
    }

    private void updateProfileFromRequest(User user, UserRequest request) {
        Profile profile = user.getProfile();

        // Create profile if it doesn't exist and there are profile fields to update
        if (profile == null && hasProfileFields(request)) {
            profile = Profile.builder()
                    .user(user)
                    .build();
            user.setProfile(profile);
        }

        if (profile != null) {
            if (request.getFirstName() != null) {
                profile.setFirstName(request.getFirstName());
            }
            if (request.getLastName() != null) {
                profile.setLastName(request.getLastName());
            }
            if (request.getAvatarUrl() != null) {
                profile.setAvatarUrl(request.getAvatarUrl());
            }
            if (request.getCountry() != null) {
                profile.setCountry(request.getCountry());
            }
            if (request.getBio() != null) {
                profile.setBio(request.getBio());
            }
        }
    }
}
