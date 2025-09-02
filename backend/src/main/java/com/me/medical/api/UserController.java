package com.me.medical.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.application.UserService;

/**
 * Controller REST responsável por gerenciar dados do perfil do usuário.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retorna dados do perfil do usuário autenticado.
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal String userEmail) {
        log.debug("GET /profile called for user: {}", userEmail);
        var profile = userService.getProfile(userEmail);
        return ResponseEntity.ok(profile);
    }

    /**
     * Atualiza dados do perfil do usuário autenticado (atualização parcial).
     */
    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal String userEmail,
            @RequestBody Map<String, Object> profileData) {
        log.debug("PATCH /profile called for user: {} with data: {}", userEmail, profileData);
        var updated = userService.updateProfile(userEmail, profileData);
        return ResponseEntity.ok(updated);
    }
}
