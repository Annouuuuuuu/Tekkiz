package com.brandonkamga.tekizz.iam.infrastructure.persistence.mapper;

import com.brandonkamga.tekizz.iam.domain.model.vo.ProviderType;
import com.brandonkamga.tekizz.iam.domain.model.vo.RoleType;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.Provider;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.Role;
import com.brandonkamga.tekizz.iam.domain.model.Profile;
import com.brandonkamga.tekizz.iam.domain.model.User;
import com.brandonkamga.tekizz.iam.domain.model.vo.Email;
import com.brandonkamga.tekizz.iam.domain.model.vo.HashedPassword;
import com.brandonkamga.tekizz.iam.domain.model.vo.UserId;
import com.brandonkamga.tekizz.iam.domain.model.vo.Username;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.ProfileJpaEntity;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.entity.UserJpaEntity;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.repository.ProviderRepository;
import com.brandonkamga.tekizz.iam.infrastructure.persistence.repository.RoleRepository;
import org.springframework.stereotype.Component;

/**
 * Maps between IAM domain model and JPA entities.
 */
@Component
public class UserPersistenceMapper {

    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;

    public UserPersistenceMapper(RoleRepository roleRepository, ProviderRepository providerRepository) {
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
    }

    /**
     * Convert a JPA entity to a domain model (reconstitute from persistence).
     */
    public User toDomain(UserJpaEntity jpa) {
        if (jpa == null) return null;

        Profile domainProfile = null;
        if (jpa.getProfile() != null) {
            ProfileJpaEntity p = jpa.getProfile();
            domainProfile = new Profile(
                    p.getId(),
                    jpa.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getAvatarUrl(),
                    p.getCountry(),
                    p.getBio()
            );
        }

        String providerName = jpa.getProvider() != null ? jpa.getProvider().getProviderName().name() : null;
        String roleName = jpa.getRole() != null ? jpa.getRole().getRoleName().name() : null;

        return User.reconstitute(
                UserId.of(jpa.getId()),
                Email.of(jpa.getEmail()),
                Username.of(jpa.getUsername()),
                HashedPassword.of(jpa.getPassword() != null ? jpa.getPassword() : ""),
                providerName,
                jpa.getProviderUserId(),
                roleName,
                domainProfile
        );
    }

    /**
     * Convert a domain model to a JPA entity (for saving).
     * Looks up Role and Provider by name from the IAM repositories.
     */
    public UserJpaEntity toJpaEntity(User domain) {
        if (domain == null) return null;

        // Resolve Provider
        Provider provider = null;
        if (domain.getProviderName() != null) {
            ProviderType providerType = ProviderType.valueOf(domain.getProviderName());
            provider = providerRepository.findByProviderName(providerType).orElse(null);
        }

        // Resolve Role
        Role role = null;
        if (domain.getRoleName() != null) {
            RoleType roleType = RoleType.valueOf(domain.getRoleName());
            role = roleRepository.findByRoleName(roleType).orElse(null);
        }

        Long rawId = domain.getId() != null ? domain.getId().value() : null;

        UserJpaEntity jpa = UserJpaEntity.builder()
                .id(rawId)
                .email(domain.getEmail().value())
                .username(domain.getUsername().value())
                .password(domain.getPassword() != null ? domain.getPassword().value() : null)
                .provider(provider)
                .providerUserId(domain.getProviderUserId())
                .role(role)
                .build();

        // Map profile
        if (domain.getProfile() != null) {
            Profile dp = domain.getProfile();
            ProfileJpaEntity profileJpa = ProfileJpaEntity.builder()
                    .id(dp.getId())
                    .user(jpa)
                    .firstName(dp.getFirstName())
                    .lastName(dp.getLastName())
                    .avatarUrl(dp.getAvatarUrl())
                    .country(dp.getCountry())
                    .bio(dp.getBio())
                    .build();
            jpa.setProfile(profileJpa);
        }

        return jpa;
    }
}
