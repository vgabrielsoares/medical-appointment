package com.me.medical.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO simples para transferir dados de appointment entre camadas.
 */
@Schema(description = "Dados de um agendamento médico")
@Getter
@Setter
@NoArgsConstructor
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
}
