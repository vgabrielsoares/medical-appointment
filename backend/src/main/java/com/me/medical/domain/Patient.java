package com.me.medical.domain;

import java.util.UUID;

/**
 * Entidade de domínio representando um paciente.
 */
public class Patient {
    private UUID id;
    private UUID userId;
    private String name;

    public Patient() {}

    public Patient(UUID id, UUID userId, String name) {
        this.id = id;
        this.userId = userId;
        this.name = name;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
