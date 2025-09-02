package com.me.medical.infra;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class JpaPatientEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private JpaUserEntity user;

    @Column(nullable = false)
    private String name;

    @Column
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaPatientEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public JpaUserEntity getUser() { return user; }
    public void setUser(JpaUserEntity user) { this.user = user; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
