package com.me.medical.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.medical.application.SlotOverlapException;
import com.me.medical.application.dto.SlotDto;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaSlotEntity;
import com.me.medical.infra.SlotRepository;

/**
 * Testes expandidos para SlotServiceImpl.
 * 
 * Além dos testes básicos existentes, cobre cenários adicionais críticos:
 * - Validações de tempo mais rigorosas
 * - Testes de atualização com sobreposição
 * - Cenários de erro e segurança
 * - Validação de metadata JSON
 */
class SlotServiceExpandedTest {

    private SlotRepository slotRepository;
    private DoctorRepository doctorRepository;
    private SlotServiceImpl service;

    @BeforeEach
    void setup() {
        slotRepository = mock(SlotRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        service = new SlotServiceImpl(slotRepository, doctorRepository);
    }

    @Test
    void createSlot_invalidTimeRange_throws() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        
        // Cenário: start igual a end
        var time = OffsetDateTime.now(ZoneOffset.UTC);
        dto.setStart(time);
        dto.setEnd(time);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("start must be before end", exception.getMessage());
        
        // Não deve acessar repositórios se validação básica falha
        verify(doctorRepository, never()).findById(any());
        verify(slotRepository, never()).findOverlappingSlots(any(), any(), any());
    }

    @Test
    void createSlot_nullStartTime_throws() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        dto.setStart(null);
        dto.setEnd(OffsetDateTime.now());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("start and end required", exception.getMessage());
    }

    @Test
    void createSlot_nullEndTime_throws() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        dto.setStart(OffsetDateTime.now());
        dto.setEnd(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("start and end required", exception.getMessage());
    }

    @Test
    void createSlot_startAfterEnd_throws() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        dto.setStart(now.plusHours(2));
        dto.setEnd(now.plusHours(1)); // end antes de start

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("start must be before end", exception.getMessage());
    }

    @Test
    void createSlot_doctorNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        var start = OffsetDateTime.now(ZoneOffset.UTC);
        dto.setStart(start);
        dto.setEnd(start.plusHours(1));

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("doctor not found", exception.getMessage());
        verify(doctorRepository).findById(doctorId);
        // Não deve verificar overlap se médico não existe
        verify(slotRepository, never()).findOverlappingSlots(any(), any(), any());
    }

    @Test
    void createSlot_withComplexMetadata_success() {
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        var start = OffsetDateTime.of(2025, 9, 5, 10, 0, 0, 0, ZoneOffset.UTC);
        dto.setStart(start);
        dto.setEnd(start.plusMinutes(45));
        
        // Metadata complexa
        var metadata = Map.of(
            "room", "Sala 203",
            "type", "consulta",
            "notes", "Primeira consulta",
            "allowWalkIn", false,
            "cost", 150.0
        );
        dto.setMetadata(metadata);

        var doctor = new JpaDoctorEntity();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(slotRepository.findOverlappingSlots(doctorId, start, dto.getEnd()))
                .thenReturn(Collections.emptyList());
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));

        SlotDto created = service.createSlot(doctorId, dto);

        assertNotNull(created);
        assertEquals(start, created.getStart());
        assertEquals("available", created.getStatus());
        
        verify(slotRepository).save(any(JpaSlotEntity.class));
    }

    @Test
    void updateSlot_success() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var dto = new SlotDto();
        var newStart = OffsetDateTime.of(2025, 9, 6, 14, 0, 0, 0, ZoneOffset.UTC);
        dto.setStart(newStart);
        dto.setEnd(newStart.plusHours(1));
        dto.setStatus("available");

        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        
        var existingSlot = new JpaSlotEntity();
        existingSlot.setDoctor(doctor);
        
        when(slotRepository.findById(slotId)).thenReturn(Optional.of(existingSlot));
        when(slotRepository.findOverlappingSlotsExcludingId(doctorId, newStart, dto.getEnd(), slotId))
                .thenReturn(Collections.emptyList());
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));

        SlotDto updated = service.updateSlot(doctorId, slotId, dto);

        assertNotNull(updated);
        verify(slotRepository).save(existingSlot);
    }

    @Test
    void updateSlot_slotNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var dto = new SlotDto();
        dto.setStart(OffsetDateTime.now());
        dto.setEnd(OffsetDateTime.now().plusHours(1));

        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.updateSlot(doctorId, slotId, dto));
        
        assertEquals("slot not found", exception.getMessage());
    }

    @Test
    void updateSlot_notOwner_throws() {
        var doctorId = UUID.randomUUID();
        var wrongDoctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var dto = new SlotDto();
        dto.setStart(OffsetDateTime.now());
        dto.setEnd(OffsetDateTime.now().plusHours(1));

        var wrongDoctor = new JpaDoctorEntity();
        wrongDoctor.setId(wrongDoctorId);
        
        var existingSlot = new JpaSlotEntity();
        existingSlot.setDoctor(wrongDoctor);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(existingSlot));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> service.updateSlot(doctorId, slotId, dto));
        
        assertEquals("not the owner", exception.getMessage());
        
        // Não deve salvar se não é proprietário
        verify(slotRepository, never()).save(any());
    }

    @Test
    void updateSlot_overlapWithOtherSlot_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var dto = new SlotDto();
        var start = OffsetDateTime.now(ZoneOffset.UTC);
        dto.setStart(start);
        dto.setEnd(start.plusHours(1));

        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        
        var existingSlot = new JpaSlotEntity();
        existingSlot.setDoctor(doctor);
        
        var overlappingSlot = new JpaSlotEntity();

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(existingSlot));
        when(slotRepository.findOverlappingSlotsExcludingId(doctorId, start, dto.getEnd(), slotId))
                .thenReturn(List.of(overlappingSlot));

        SlotOverlapException exception = assertThrows(SlotOverlapException.class,
                () -> service.updateSlot(doctorId, slotId, dto));
        
        assertEquals("slot overlaps with existing slot", exception.getMessage());
        verify(slotRepository, never()).save(any());
    }

    @Test
    void deleteSlot_success() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();

        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        
        var slot = new JpaSlotEntity();
        slot.setDoctor(doctor);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        service.deleteSlot(doctorId, slotId);

        verify(slotRepository).delete(slot);
    }

    @Test
    void deleteSlot_slotNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();

        when(slotRepository.findById(slotId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.deleteSlot(doctorId, slotId));
        
        assertEquals("slot not found", exception.getMessage());
        verify(slotRepository, never()).delete(any());
    }

    @Test
    void deleteSlot_notOwner_throws() {
        var doctorId = UUID.randomUUID();
        var wrongDoctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();

        var wrongDoctor = new JpaDoctorEntity();
        wrongDoctor.setId(wrongDoctorId);
        
        var slot = new JpaSlotEntity();
        slot.setDoctor(wrongDoctor);

        when(slotRepository.findById(slotId)).thenReturn(Optional.of(slot));

        SecurityException exception = assertThrows(SecurityException.class,
                () -> service.deleteSlot(doctorId, slotId));
        
        assertEquals("not the owner", exception.getMessage());
        verify(slotRepository, never()).delete(any());
    }

    @Test
    void listSlots_success() {
        var doctorId = UUID.randomUUID();
        var slot1 = new JpaSlotEntity();
        var slot2 = new JpaSlotEntity();

        when(slotRepository.findByDoctorId(doctorId)).thenReturn(List.of(slot1, slot2));

        List<SlotDto> slots = service.listSlots(doctorId);

        assertEquals(2, slots.size());
        verify(slotRepository).findByDoctorId(doctorId);
    }

    @Test
    void listSlots_emptyResult() {
        var doctorId = UUID.randomUUID();

        when(slotRepository.findByDoctorId(doctorId)).thenReturn(Collections.emptyList());

        List<SlotDto> slots = service.listSlots(doctorId);

        assertEquals(0, slots.size());
        assertNotNull(slots); // deve retornar lista vazia, não null
    }

    @Test
    void createSlot_multipleOverlappingSlots_throws() {
        // Teste cenário onde há múltiplos slots sobrepostos
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        var start = OffsetDateTime.now(ZoneOffset.UTC);
        dto.setStart(start);
        dto.setEnd(start.plusHours(2));

        var doctor = new JpaDoctorEntity();
        var overlapping1 = new JpaSlotEntity();
        var overlapping2 = new JpaSlotEntity();

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(slotRepository.findOverlappingSlots(doctorId, start, dto.getEnd()))
                .thenReturn(List.of(overlapping1, overlapping2));

        SlotOverlapException exception = assertThrows(SlotOverlapException.class,
                () -> service.createSlot(doctorId, dto));
        
        assertEquals("slot overlaps with existing slot", exception.getMessage());
        verify(slotRepository, never()).save(any());
    }

    @Test
    void createSlot_exactBoundaryTimes_success() {
        // Teste slots com horários exatos (mesmo minuto)
        var doctorId = UUID.randomUUID();
        var dto = new SlotDto();
        var start = OffsetDateTime.of(2025, 9, 7, 9, 0, 0, 0, ZoneOffset.UTC);
        var end = OffsetDateTime.of(2025, 9, 7, 10, 0, 0, 0, ZoneOffset.UTC);
        dto.setStart(start);
        dto.setEnd(end);

        var doctor = new JpaDoctorEntity();
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(slotRepository.findOverlappingSlots(doctorId, start, end))
                .thenReturn(Collections.emptyList());
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));

        SlotDto created = service.createSlot(doctorId, dto);

        assertEquals(start, created.getStart());
        assertEquals(end, created.getEnd());
        assertEquals("available", created.getStatus());
    }
}
