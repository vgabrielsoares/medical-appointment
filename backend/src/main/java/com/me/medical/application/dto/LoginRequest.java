package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO para login de usuários.
 */
@Schema(description = "Dados para autenticação de usuário")
public class LoginRequest {
    
    @Schema(description = "Email do usuário", example = "doctor@example.com", required = true)
    private String email;
    
    @Schema(description = "Senha do usuário", example = "doctorpass", required = true)
    private String password;

    public LoginRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
