package com.me.medical.application.dto;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request DTO usado para criar um agendamento a partir da API.
 */
@Schema(description = "Dados para criação de um agendamento")
@Getter
@Setter
@NoArgsConstructor
public class CreateAppointmentRequest {
    
    @Schema(description = "ID do médico", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private UUID doctorId;
    
    @Schema(description = "ID do slot/horário disponível", example = "550e8400-e29b-41d4-a716-446655440001", required = true)
    private UUID slotId;
}
