package com.me.medical.application.dto;

import java.util.UUID;

/**
 * Request DTO usado para criar um agendamento a partir da API.
 */
public class CreateAppointmentRequest {
    private UUID doctorId;
    private UUID slotId;

    public CreateAppointmentRequest() {}

    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }

    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
}
