package com.me.medical.application.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.me.medical.application.SlotOverlapException;
import com.me.medical.application.SlotService;
import com.me.medical.application.dto.SlotDto;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaSlotEntity;
import com.me.medical.infra.SlotRepository;

/**
 * Implementação do SlotService contendo validações de negócio.
 * Regras importantes: start < end; apenas o médico dono pode gerenciar; sem sobreposição.
 */
@Service
public class SlotServiceImpl implements SlotService {
    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SlotServiceImpl(SlotRepository slotRepository, DoctorRepository doctorRepository) {
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    @Transactional
    public SlotDto createSlot(UUID doctorId, SlotDto dto) {
        validateTimes(dto.getStart(), dto.getEnd());

        var doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("doctor not found"));
        // validação de overlap: usar query que delega a lógica ao banco
        var overlapping = slotRepository.findOverlappingSlots(doctorId, dto.getStart(), dto.getEnd());
        if (!overlapping.isEmpty()) {
            throw new SlotOverlapException("slot overlaps with existing slot");
        }

        var entity = new JpaSlotEntity();
        entity.setId(UUID.randomUUID());
        entity.setDoctor(doctor);
        entity.setStartTime(dto.getStart());
        entity.setEndTime(dto.getEnd());
        entity.setStatus(dto.getStatus() == null ? "available" : dto.getStatus());
        try {
            entity.setMetadata(dto.getMetadata() == null ? null : objectMapper.writeValueAsString(dto.getMetadata()));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("invalid metadata JSON", e);
        }
        entity.setCreatedAt(OffsetDateTime.now());

        var saved = slotRepository.save(entity);
        return toDto(saved);
    }

    @Override
    public List<SlotDto> listSlots(UUID doctorId) {
        return slotRepository.findByDoctorId(doctorId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SlotDto updateSlot(UUID doctorId, UUID slotId, SlotDto dto) {
        validateTimes(dto.getStart(), dto.getEnd());

        var entity = slotRepository.findById(slotId)
            .orElseThrow(() -> new IllegalArgumentException("slot not found"));

        if (!entity.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("not the owner");
        }

        // checa overlap com exclusão própria usando query parametrizada
        var overlapping = slotRepository.findOverlappingSlotsExcludingId(doctorId, dto.getStart(), dto.getEnd(), slotId);
        if (!overlapping.isEmpty()) {
            throw new SlotOverlapException("slot overlaps with existing slot");
        }

        entity.setStartTime(dto.getStart());
        entity.setEndTime(dto.getEnd());
        if (dto.getStatus() != null) entity.setStatus(dto.getStatus());
        if (dto.getMetadata() != null) {
            try {
                entity.setMetadata(objectMapper.writeValueAsString(dto.getMetadata()));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("invalid metadata JSON", e);
            }
        }

        var saved = slotRepository.save(entity);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteSlot(UUID doctorId, UUID slotId) {
        var entity = slotRepository.findById(slotId)
            .orElseThrow(() -> new IllegalArgumentException("slot not found"));
        if (!entity.getDoctor().getId().equals(doctorId)) {
            throw new SecurityException("not the owner");
        }
        slotRepository.delete(entity);
    }

    private void validateTimes(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) throw new IllegalArgumentException("start and end required");
        if (!start.isBefore(end)) throw new IllegalArgumentException("start must be before end");
    }

    private boolean overlaps(OffsetDateTime aStart, OffsetDateTime aEnd, OffsetDateTime bStart, OffsetDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private SlotDto toDto(JpaSlotEntity e) {
        var d = new SlotDto();
        d.setId(e.getId());
        d.setStart(e.getStartTime());
        d.setEnd(e.getEndTime());
        d.setStatus(e.getStatus());
        var meta = e.getMetadata();
        if (meta == null) d.setMetadata(null);
        else {
            try {
                d.setMetadata(objectMapper.readValue(meta, Object.class));
            } catch (Exception ex) {
                // fallback
                d.setMetadata(meta);
            }
        }
        return d;
    }
}
