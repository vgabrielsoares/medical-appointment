package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response DTO para operações de autenticação.
 */
@Schema(description = "Resposta de autenticação contendo token JWT e informações do usuário")
public class AuthResponse {
    
    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Email do usuário autenticado", example = "doctor@example.com")
    private String email;
    
    @Schema(description = "Papel/role do usuário", example = "ROLE_DOCTOR")
    private String role;
    
    @Schema(description = "Nome do usuário", example = "Dr. Example")
    private String name;

    public AuthResponse() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
