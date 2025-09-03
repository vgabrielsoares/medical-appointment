package com.me.medical.infra;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class JpaRoleEntity {
    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
