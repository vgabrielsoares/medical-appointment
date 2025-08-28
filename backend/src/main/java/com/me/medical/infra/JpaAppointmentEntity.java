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
 * Entidade JPA representando um agendamento (appointment).
 *
 * Mapeia para a tabela `appointments` criada pela migration V4.
 */
@Entity
@Table(name = "appointments")
public class JpaAppointmentEntity {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "slot_id")
    private JpaSlotEntity slot;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private JpaDoctorEntity doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private JpaPatientEntity patient;

    @Column(nullable = false)
    private String status; // confirmed | cancelled

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    public JpaAppointmentEntity() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public JpaSlotEntity getSlot() { return slot; }
    public void setSlot(JpaSlotEntity slot) { this.slot = slot; }

    public JpaDoctorEntity getDoctor() { return doctor; }
    public void setDoctor(JpaDoctorEntity doctor) { this.doctor = doctor; }

    public JpaPatientEntity getPatient() { return patient; }
    public void setPatient(JpaPatientEntity patient) { this.patient = patient; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
