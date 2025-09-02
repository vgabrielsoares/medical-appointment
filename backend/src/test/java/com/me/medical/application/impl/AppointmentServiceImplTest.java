package com.me.medical.application.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.me.medical.application.dto.AppointmentDto;
import com.me.medical.infra.AppointmentRepository;
import com.me.medical.infra.JpaAppointmentEntity;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaSlotEntity;

import jakarta.persistence.EntityManager;

class AppointmentServiceImplTest {
    private AppointmentRepository appointmentRepository;
    private EntityManager entityManager;
    private AppointmentServiceImpl service;

    @BeforeEach
    void setup() {
        appointmentRepository = mock(AppointmentRepository.class);
        entityManager = mock(EntityManager.class);
        service = new AppointmentServiceImpl(appointmentRepository, entityManager, null, null, null);
    }

    @Test
    void listByPatient_mapsEntitiesToDto() {
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(UUID.randomUUID());

        var doctor = new JpaDoctorEntity();
        doctor.setId(UUID.randomUUID());

        var patient = new JpaPatientEntity();
        patient.setId(patientId);

        var appt = new JpaAppointmentEntity();
        appt.setId(UUID.randomUUID());
        appt.setSlot(slot);
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setStatus("confirmed");
        appt.setCreatedAt(OffsetDateTime.now());

        when(appointmentRepository.findByPatientId(patientId)).thenReturn(List.of(appt));

        List<AppointmentDto> list = service.listByPatient(patientId);

        assertNotNull(list);
        assertEquals(1, list.size());
        var dto = list.get(0);
        assertEquals(appt.getId(), dto.getId());
        assertEquals(slot.getId(), dto.getSlotId());
        assertEquals(doctor.getId(), dto.getDoctorId());
        assertEquals(patient.getId(), dto.getPatientId());
        assertEquals(appt.getStatus(), dto.getStatus());
        assertNotNull(dto.getCreatedAt());

        verify(appointmentRepository, times(1)).findByPatientId(patientId);
    }
}
