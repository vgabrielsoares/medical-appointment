package com.me.medical.application.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request DTO usado para criar um agendamento a partir da API.
 */
@Schema(description = "Dados para criação de um agendamento")
public class CreateAppointmentRequest {
    
    @Schema(description = "ID do médico", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID doctorId;
    
    @Schema(description = "ID do slot/horário disponível", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
    private UUID slotId;

    public CreateAppointmentRequest() {}

    public UUID getDoctorId() { return doctorId; }
    public void setDoctorId(UUID doctorId) { this.doctorId = doctorId; }

    public UUID getSlotId() { return slotId; }
    public void setSlotId(UUID slotId) { this.slotId = slotId; }
}
