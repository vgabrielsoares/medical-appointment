package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO para login de usuários.
 */
@Schema(description = "Dados para autenticação de usuário")
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    
    @Schema(description = "Email do usuário", example = "doctor@example.com", required = true)
    private String email;
    
    @Schema(description = "Senha do usuário", example = "doctorpass", required = true)
    private String password;
}
