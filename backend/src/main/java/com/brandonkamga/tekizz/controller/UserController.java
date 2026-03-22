package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.UserRequest;
import com.brandonkamga.tekizz.dto.UserResponse;
import com.brandonkamga.tekizz.exception.BadRequestException;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.mapper.UserMapper;
import com.brandonkamga.tekizz.service.interfaces.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        return userService.findByIdOptional(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user))))
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("User", "id", 0L);
        }
        
        User user = userService.findByEmail(authentication.getName());
        if (user == null) {
            throw new ResourceNotFoundException("User", "email", authentication.getName());
        }
        return ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user)));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {
        User user = userService.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User", "email", email);
        }
        return ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(user)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest userRequest) {
        
        // Users can only update their own account, admins cannot update users
        if (!isCurrentUser(id)) {
            throw new BadRequestException("You can only update your own account");
        }

        if (!userService.findByIdOptional(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        UserRequest validatedRequest = validateUpdateRequest(id, userRequest);
        User existingUser = userService.findById(id);
        User updatedUser = userMapper.updateEntity(existingUser, validatedRequest);
        User savedUser = userService.save(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(userMapper.toResponse(savedUser), "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userService.findByIdOptional(id).isPresent()) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        // Admins can delete any user, users can only delete their own account
        if (!isCurrentUser(id) && !isCurrentUserAdmin()) {
            throw new BadRequestException("You can only delete your own account");
        }

        userService.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    private boolean isCurrentUser(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        
        User currentUser = userService.findByEmail(authentication.getName());
        return currentUser != null && currentUser.getId().equals(userId);
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals(RoleType.ADMIN.name()));
    }

    private UserRequest validateUpdateRequest(Long id, UserRequest request) {
        if (request.getEmail() != null && userService.existsByEmailExcept(id, request.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        if (request.getUsername() != null && userService.existsByUsernameExcept(id, request.getUsername())) {
            throw new BadRequestException("Username already exists");
        }
        return request;
    }
}
