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
 * Entidade JPA representando um agendamento (appointment).
 *
 * Mapeia para a tabela `appointments` criada pela migration V4.
 */
@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
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
}
