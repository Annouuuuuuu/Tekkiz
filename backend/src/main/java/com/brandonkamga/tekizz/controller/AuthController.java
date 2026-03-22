package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.Profile;
import com.brandonkamga.tekizz.domain.Provider;
import com.brandonkamga.tekizz.domain.ProviderType;
import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.AuthResponse;
import com.brandonkamga.tekizz.dto.LoginRequest;
import com.brandonkamga.tekizz.dto.UserRequest;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.mapper.UserMapper;
import com.brandonkamga.tekizz.repository.ProviderRepository;
import com.brandonkamga.tekizz.repository.RoleRepository;
import com.brandonkamga.tekizz.security.jwt.JwtService;
import com.brandonkamga.tekizz.service.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ProviderRepository providerRepository;

    public AuthController(
            UserService userService,
            UserMapper userMapper,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            ProviderRepository providerRepository) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.providerRepository = providerRepository;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody UserRequest userRequest) {
        if (userRequest.getEmail() == null || userService.existsByEmail(userRequest.getEmail())) {
            throw new BadRequestException("Email already exists or is required");
        }
        if (userRequest.getUsername() == null || userService.existsByUsername(userRequest.getUsername())) {
            throw new BadRequestException("Username already exists or is required");
        }
        if (userRequest.getPassword() == null || userRequest.getPassword().length() < 6) {
            throw new BadRequestException("Password is required and must be at least 6 characters");
        }

        Role role = roleRepository.findByRoleName(RoleType.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleType.USER));

        Provider localProvider = providerRepository.findByProviderName(ProviderType.LOCAL)
                .orElseGet(() -> providerRepository.save(Provider.builder()
                        .providerName(ProviderType.LOCAL)
                        .description("Local authentication")
                        .build()));

        User user = new User();
        user.setEmail(userRequest.getEmail());
        user.setUsername(userRequest.getUsername());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setProvider(localProvider);
        user.setProviderUserId("local-" + userRequest.getEmail());
        user.setRole(role);

        // Create profile with firstName and lastName
        Profile profile = Profile.builder()
                .user(user)
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();
        user.setProfile(profile);

        User savedUser = userService.save(user);

        String token = jwtService.generateTokenForUser(savedUser.getEmail(), ProviderType.LOCAL.name());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(AuthResponse.of(token, userMapper.toResponse(savedUser)), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            User user = userService.findByEmail(loginRequest.getEmail());
            if (user == null) {
                throw new BadRequestException("User not found");
            }

            String providerName = user.getProvider() != null ? user.getProvider().getProviderName().name() : ProviderType.LOCAL.name();
            String token = jwtService.generateTokenForUser(user.getEmail(), providerName);

            return ResponseEntity.ok(ApiResponse.success(AuthResponse.of(token, userMapper.toResponse(user)), "Login successful"));
        } catch (BadCredentialsException e) {
            throw new BadRequestException("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}
