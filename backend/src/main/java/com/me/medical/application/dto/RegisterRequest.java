package com.me.medical.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO para registro de novos usuários.
 */
@Schema(description = "Dados para registro de novo usuário")
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

    public RegisterRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
