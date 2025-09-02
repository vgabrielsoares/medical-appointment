package com.me.medical.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO simples para transferir dados de appointment entre camadas.
 */
@Schema(description = "Dados de um agendamento médico")
public class AppointmentDto {
    
    @Schema(description = "ID único do agendamento", example = "550e8400-e29b-41d4-a716-446655440002")
    private UUID id;
    
    @Schema(description = "ID do slot/horário agendado", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID slotId;
    
    @Schema(description = "ID do médico", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID doctorId;
    
    @Schema(description = "ID do paciente", example = "550e8400-e29b-41d4-a716-446655440003")
    private UUID patientId;
    
    @Schema(description = "Status do agendamento", example = "confirmed", allowableValues = {"confirmed", "cancelled", "completed"})
    private String status;
    
    @Schema(description = "Data e hora de criação do agendamento", example = "2025-09-02T15:30:00-03:00")
    private OffsetDateTime createdAt;
    
    @Schema(description = "Nome do médico", example = "Dr. Example")
    private String doctorName;
    
    @Schema(description = "Especialidade do médico", example = "Cardiologia")
    private String doctorSpecialty;
    
    @Schema(description = "Data e hora de início da consulta", example = "2025-09-03T09:00:00-03:00")
    private OffsetDateTime start;
    
    @Schema(description = "Data e hora de fim da consulta", example = "2025-09-03T10:00:00-03:00")
    private OffsetDateTime end;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }
    public UUID getPatientId() { return patientId; }
    public void setPatientId(UUID patientId) { this.patientId = patientId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public String getDoctorSpecialty() { return doctorSpecialty; }
    public void setDoctorSpecialty(String doctorSpecialty) { this.doctorSpecialty = doctorSpecialty; }
    public OffsetDateTime getStart() { return start; }
    public void setStart(OffsetDateTime start) { this.start = start; }
    public OffsetDateTime getEnd() { return end; }
    public void setEnd(OffsetDateTime end) { this.end = end; }
}
