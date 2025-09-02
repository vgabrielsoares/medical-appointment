package com.me.medical.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO representando um slot para entrada/saída da API.
 */
@Schema(description = "Dados de um slot/horário disponível")
public class SlotDto {
    
    @Schema(description = "ID único do slot", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;
    
    @Schema(description = "Data e hora de início do slot", example = "2025-09-03T09:00:00-03:00", required = true)
    private OffsetDateTime start;
    
    @Schema(description = "Data e hora de fim do slot", example = "2025-09-03T10:00:00-03:00", required = true)
    private OffsetDateTime end;
    
    @Schema(description = "Status do slot", example = "available", allowableValues = {"available", "booked"})
    private String status;
    
    @Schema(description = "Metadados adicionais do slot", example = "{\"notes\": \"Consulta de rotina\"}")
    private Object metadata;

    public SlotDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public OffsetDateTime getStart() { return start; }
    public void setStart(OffsetDateTime start) { this.start = start; }

    public OffsetDateTime getEnd() { return end; }
    public void setEnd(OffsetDateTime end) { this.end = end; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Object getMetadata() { return metadata; }
    public void setMetadata(Object metadata) { this.metadata = metadata; }
}
