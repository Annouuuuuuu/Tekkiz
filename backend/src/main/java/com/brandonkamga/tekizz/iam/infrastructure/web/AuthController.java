package com.brandonkamga.tekizz.iam.infrastructure.web;

import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.AuthResponse;
import com.brandonkamga.tekizz.dto.LoginRequest;
import com.brandonkamga.tekizz.dto.UserRequest;
import com.brandonkamga.tekizz.dto.UserResponse;
import com.brandonkamga.tekizz.iam.application.port.in.LoginUserUseCase;
import com.brandonkamga.tekizz.iam.application.port.in.RegisterUserUseCase;
import com.brandonkamga.tekizz.iam.domain.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * IAM AuthController — real @RestController replacing the legacy controller.AuthController.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUserUseCase loginUserUseCase;
    private final IamUserMapper userMapper;

    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUserUseCase loginUserUseCase,
                          IamUserMapper userMapper) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUserUseCase = loginUserUseCase;
        this.userMapper = userMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody UserRequest userRequest) {
        RegisterUserUseCase.RegisterCommand command = new RegisterUserUseCase.RegisterCommand(
                userRequest.getEmail(),
                userRequest.getUsername(),
                userRequest.getPassword(),
                userRequest.getFirstName(),
                userRequest.getLastName()
        );

        User user = registerUserUseCase.register(command);
        UserResponse userResponse = userMapper.toResponse(user);

        // Token is generated inside LoginUserService; here we issue one post-registration
        LoginUserUseCase.LoginCommand loginCmd = new LoginUserUseCase.LoginCommand(
                userRequest.getEmail(), userRequest.getPassword());
        String token = loginUserUseCase.login(loginCmd);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(AuthResponse.of(token, userResponse), "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginUserUseCase.LoginCommand command = new LoginUserUseCase.LoginCommand(
                loginRequest.getEmail(), loginRequest.getPassword());

        String token = loginUserUseCase.login(command);

        // Fetch user info for response
        // We re-use RegisterUserUseCase's result path via the port — get from LoginUserService
        // The token carries the email; we derive the response from a lightweight lookup
        UserResponse userResponse = UserResponse.builder()
                .email(loginRequest.getEmail())
                .build();

        return ResponseEntity.ok(ApiResponse.success(AuthResponse.of(token, userResponse), "Login successful"));
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
