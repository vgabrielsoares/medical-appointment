package com.me.medical.infra;

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

/**
 * Entidade JPA representando o slot de disponibilidade de um médico.
 *
 * Mantemos a entidade leve e mapeada para a tabela `slots` criada pela migration.
 */
@Entity
@Table(name = "slots")
@Getter
@Setter
@NoArgsConstructor
public class JpaSlotEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private JpaDoctorEntity doctor;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(nullable = false)
    private String status; // available | booked | cancelled

    @Column(columnDefinition = "jsonb")
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    private String metadata;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;
}
