package com.brandonkamga.tekizz.iam.application.service;

import com.brandonkamga.tekizz.domain.*;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.iam.application.port.in.RegisterUserUseCase;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.repository.RoleRepository;
import com.brandonkamga.tekizz.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterUserService implements RegisterUserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserRepository userRepository,
                                RoleRepository roleRepository,
                                ProviderRepository providerRepository,
                                PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        if (userRepository.findByUsername(command.username()).isPresent()) {
            throw new BadRequestException("Username already exists");
        }

        Role role = roleRepository.findByRoleName(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleType.USER));

        Provider localProvider = providerRepository.findByProviderName(ProviderType.LOCAL)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "name", ProviderType.LOCAL));

        User user = User.builder()
                .email(command.email())
                .username(command.username())
                .password(passwordEncoder.encode(command.password()))
                .provider(localProvider)
                .providerUserId("local-" + command.email())
                .role(role)
                .build();

        Profile profile = Profile.builder()
                .user(user)
                .firstName(command.firstName())
                .lastName(command.lastName())
                .build();
        user.setProfile(profile);

        return userRepository.save(user);
    }
}
