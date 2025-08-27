package com.me.medical.domain;

import java.util.UUID;

/**
 * Entidade de domínio representando um usuário do sistema.
 *
 * Esta classe é intencionalmente simples e sem dependências de framework
 * para preservar as camadas internas da Clean Architecture.
 */
public class User {
    private UUID id;
    private String email;
    private String role;

    public User() {}

    public User(UUID id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
