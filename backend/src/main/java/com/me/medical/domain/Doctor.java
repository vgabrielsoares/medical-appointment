package com.me.medical.domain;

import java.util.UUID;

/**
 * Entidade de domínio representando um médico.
 */
public class Doctor {
    private UUID id;
    private UUID userId;
    private String name;
    private String specialty;

    public Doctor() {}

    public Doctor(UUID id, UUID userId, String name, String specialty) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.specialty = specialty;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
}
