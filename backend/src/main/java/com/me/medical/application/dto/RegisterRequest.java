package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO para registro de novos usuários.
 */
@Schema(description = "Dados para registro de novo usuário")
@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {
    
    @Schema(description = "Nome completo do usuário", example = "Dr. Example", required = true)
    private String name;
    
    @Schema(description = "Email do usuário", example = "newdoctor@example.com", required = true)
    private String email;
    
    @Schema(description = "Senha do usuário", example = "newpassword", required = true)
    private String password;
    
    @Schema(description = "Tipo de usuário", example = "ROLE_DOCTOR", allowableValues = {"ROLE_DOCTOR", "ROLE_PATIENT"}, required = true)
    private String role;
    
    @Schema(description = "Especialidade médica (obrigatório para médicos)", example = "Cardiologia")
    private String specialty;
    
    @Schema(description = "Telefone de contato", example = "(11) 99999-9999")
    private String phone;
}
