package com.me.medical.infra;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Entidade JPA representando o slot de disponibilidade de um médico.
 *
 * Mantemos a entidade leve e mapeada para a tabela `slots` criada pela migration.
 */
@Entity
@Table(name = "slots")
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
    private String metadata;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaSlotEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public JpaDoctorEntity getDoctor() { return doctor; }
    public void setDoctor(JpaDoctorEntity doctor) { this.doctor = doctor; }

    public OffsetDateTime getStartTime() { return startTime; }
    public void setStartTime(OffsetDateTime startTime) { this.startTime = startTime; }

    public OffsetDateTime getEndTime() { return endTime; }
    public void setEndTime(OffsetDateTime endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
