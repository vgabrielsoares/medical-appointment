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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
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
}
