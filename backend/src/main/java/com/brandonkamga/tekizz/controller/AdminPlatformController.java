package com.brandonkamga.tekizz.controller;

import com.brandonkamga.tekizz.domain.Role;
import com.brandonkamga.tekizz.domain.RoleType;
import com.brandonkamga.tekizz.domain.User;
import com.brandonkamga.tekizz.dto.ApiResponse;
import com.brandonkamga.tekizz.dto.admin.AdminRoleUpdateRequest;
import com.brandonkamga.tekizz.dto.admin.AdminStatsResponse;
import com.brandonkamga.tekizz.exception.ResourceNotFoundException;
import com.brandonkamga.tekizz.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/platform")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlatformController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final QuestionRepository questionRepository;
    private final CategoryRepository categoryRepository;
    private final GameSessionRepository gameSessionRepository;
    private final SmatchDeckRepository smatchDeckRepository;
    private final SmatchPairRepository smatchPairRepository;
    private final SmatchSessionRepository smatchSessionRepository;

    public AdminPlatformController(UserRepository userRepository,
                                   RoleRepository roleRepository,
                                   QuestionRepository questionRepository,
                                   CategoryRepository categoryRepository,
                                   GameSessionRepository gameSessionRepository,
                                   SmatchDeckRepository smatchDeckRepository,
                                   SmatchPairRepository smatchPairRepository,
                                   SmatchSessionRepository smatchSessionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.questionRepository = questionRepository;
        this.categoryRepository = categoryRepository;
        this.gameSessionRepository = gameSessionRepository;
        this.smatchDeckRepository = smatchDeckRepository;
        this.smatchPairRepository = smatchPairRepository;
        this.smatchSessionRepository = smatchSessionRepository;
    }

    @GetMapping("/stats")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<AdminStatsResponse>> getStats() {
        long totalUsers = userRepository.count();
        long totalQuestions = questionRepository.count();
        long activeQuestions = questionRepository.findByStatus(com.brandonkamga.tekizz.domain.QuestionStatusType.ACTIVE).size();
        long totalCategories = categoryRepository.count();
        long totalQcmSessions = gameSessionRepository.count();
        long activeQcmSessions = gameSessionRepository.findByCompletedAtIsNotNull().size();

        long totalDecks = smatchDeckRepository.count();
        long activeDecks = smatchDeckRepository.countActive();
        long totalPairs = smatchPairRepository.count();
        long totalSmatchSessions = smatchSessionRepository.count();
        long activeSmatchSessions = smatchSessionRepository.countByCompletedAtIsNull();

        AdminStatsResponse stats = AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalQcmQuestions(totalQuestions)
                .activeQcmQuestions(activeQuestions)
                .totalQcmCategories(totalCategories)
                .totalQcmSessions(totalQcmSessions)
                .activeQcmSessions(activeQcmSessions)
                .totalSmatchDecks(totalDecks)
                .activeSmatchDecks(activeDecks)
                .totalSmatchPairs(totalPairs)
                .totalSmatchSessions(totalSmatchSessions)
                .activeSmatchSessions(activeSmatchSessions)
                .build();

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/users")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getUsers(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) String role) {

        List<User> users = userRepository.findAll();

        List<Map<String, Object>> result = users.stream()
                .filter(u -> search.isEmpty()
                        || u.getUsername().toLowerCase().contains(search.toLowerCase())
                        || u.getEmail().toLowerCase().contains(search.toLowerCase()))
                .filter(u -> role == null || role.isEmpty()
                        || (u.getRole() != null && u.getRole().getRoleName().name().equals(role)))
                .map(u -> {
                    var m = new java.util.LinkedHashMap<String, Object>();
                    m.put("id", u.getId());
                    m.put("username", u.getUsername());
                    m.put("email", u.getEmail());
                    m.put("roleName", u.getRole() != null ? u.getRole().getRoleName().name() : "USER");
                    m.put("providerName", u.getProvider() != null ? u.getProvider().getProviderName().name() : "LOCAL");
                    m.put("createdAt", u.getCreatedAt());
                    if (u.getProfile() != null) {
                        m.put("firstName", u.getProfile().getFirstName());
                        m.put("lastName", u.getProfile().getLastName());
                        m.put("avatarUrl", u.getProfile().getAvatarUrl());
                    }
                    return m;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/users/{id}/role")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody AdminRoleUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Prevent admin from demoting themselves
        if (userDetails != null && user.getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot change your own role"));
        }

        RoleType roleType = RoleType.valueOf(request.getRoleName().toUpperCase());
        Role role = roleRepository.findByRoleName(roleType)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", request.getRoleName()));

        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(null, "Role updated successfully"));
    }

    @DeleteMapping("/users/{id}")
    @Transactional
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userDetails != null && user.getEmail().equals(userDetails.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Cannot delete your own account"));
        }

        userRepository.delete(user);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}
