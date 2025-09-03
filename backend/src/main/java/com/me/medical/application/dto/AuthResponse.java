package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO para operações de autenticação.
 */
@Schema(description = "Resposta de autenticação contendo token JWT e informações do usuário")
@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {
    
    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Email do usuário autenticado", example = "doctor@example.com")
    private String email;
    
    @Schema(description = "Papel/role do usuário", example = "ROLE_DOCTOR")
    private String role;
    
    @Schema(description = "Nome do usuário", example = "Dr. Example")
    private String name;
}
