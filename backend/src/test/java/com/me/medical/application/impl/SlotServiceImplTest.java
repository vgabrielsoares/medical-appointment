package com.me.medical.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
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

import static org.mockito.Mockito.mock;

class SlotServiceImplTest {
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
    void createSlot_success() {
        var doctorId = UUID.randomUUID();

        var dto = new SlotDto();
        var start = OffsetDateTime.of(2025, 9, 2, 9, 0, 0, 0, ZoneOffset.UTC);
        var end = start.plusHours(1);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setMetadata(Map.of("room", "A1"));

        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(slotRepository.findOverlappingSlots(doctorId, start, end)).thenReturn(Collections.emptyList());
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));

        var created = service.createSlot(doctorId, dto);

        assertEquals(start, created.getStart());
        assertEquals(end, created.getEnd());
        assertEquals("available", created.getStatus());
    }

    @Test
    void createSlot_overlap_throws() {
        var doctorId = UUID.randomUUID();

        var dto = new SlotDto();
        var start = OffsetDateTime.now(ZoneOffset.UTC);
        var end = start.plusMinutes(30);
        dto.setStart(start);
        dto.setEnd(end);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(new JpaDoctorEntity()));
        when(slotRepository.findOverlappingSlots(doctorId, start, end))
                .thenReturn(Collections.singletonList(new JpaSlotEntity()));

        assertThrows(SlotOverlapException.class, () -> service.createSlot(doctorId, dto));
    }
}
