package com.me.medical.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO simples para transferir dados de appointment entre camadas.
 */
public class AppointmentDto {
    private UUID id;
    private UUID slotId;
    private UUID doctorId;
    private UUID patientId;
    private String status;
    private OffsetDateTime createdAt;
    private String doctorName;
    private String doctorSpecialty;
    private OffsetDateTime start;
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
