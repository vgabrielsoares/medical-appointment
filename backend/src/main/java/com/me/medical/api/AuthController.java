package com.me.medical.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.application.AuthService;
import com.me.medical.application.dto.LoginRequest;
import com.me.medical.application.dto.RegisterRequest;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsável por operações de autenticação e registro de usuários.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Autentica um usuário e retorna token JWT.
     * Credenciais seed: doctor@example.com/doctorpass ou patient@example.com/patientpass
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        var response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * Registra um novo usuário no sistema (médico ou paciente).
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        var response = authService.register(
            request.getName(), 
            request.getEmail(), 
            request.getPassword(), 
            request.getRole(), 
            request.getSpecialty(), 
            request.getPhone()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica se um email já está cadastrado no sistema.
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = authService.emailExists(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
