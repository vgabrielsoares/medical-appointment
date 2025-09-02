package com.me.medical.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.medical.application.dto.AppointmentDto;
import com.me.medical.infra.AppointmentRepository;
import com.me.medical.infra.JpaAppointmentEntity;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaSlotEntity;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.SlotRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

class AppointmentServiceCreateTest {
    private AppointmentRepository appointmentRepository;
    private EntityManager entityManager;
    private SlotRepository slotRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private AppointmentServiceImpl service;

    @BeforeEach
    void setup() {
        appointmentRepository = mock(AppointmentRepository.class);
        entityManager = mock(EntityManager.class);
        slotRepository = mock(SlotRepository.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);

        service = new AppointmentServiceImpl(appointmentRepository, entityManager, slotRepository, doctorRepository,
                patientRepository);
    }

    @Test
    void createAppointment_success() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var doc = new JpaDoctorEntity();
        doc.setId(doctorId);
        slot.setDoctor(doc);
        slot.setStatus("available");
        slot.setStartTime(OffsetDateTime.of(2025, 9, 2, 10, 0, 0, 0, ZoneOffset.UTC));
        slot.setEndTime(slot.getStartTime().plusMinutes(30));

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(slot);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doc));
        var patient = new JpaPatientEntity();
        patient.setId(patientId);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(appointmentRepository.save(any(JpaAppointmentEntity.class))).thenAnswer(i -> i.getArgument(0));

        AppointmentDto dto = service.createAppointment(doctorId, slotId, patientId);

        assertEquals(slotId, dto.getSlotId());
        assertEquals(doctorId, dto.getDoctorId());
        assertEquals(patientId, dto.getPatientId());
        assertEquals("confirmed", dto.getStatus());
    }

    @Test
    void createAppointment_slotNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> service.createAppointment(doctorId, slotId, patientId));
    }

    @Test
    void createAppointment_slotNotAvailable_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var doc = new JpaDoctorEntity();
        doc.setId(doctorId);
        slot.setDoctor(doc);
        slot.setStatus("booked");

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(slot);

        assertThrows(IllegalStateException.class, () -> service.createAppointment(doctorId, slotId, patientId));
    }
}
