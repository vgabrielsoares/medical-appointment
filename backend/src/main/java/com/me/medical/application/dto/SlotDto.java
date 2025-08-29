package com.me.medical.application.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representando um slot para entrada/saída da API.
 */
public class SlotDto {
    private UUID id;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String status;
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
