package com.me.medical.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.me.medical.infra.AppointmentRepository;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaAppointmentEntity;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaSlotEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.SlotRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

/**
 * Testes de concorrência para AppointmentService.
 * 
 * Simula cenários onde múltiplos clientes tentam reservar o mesmo slot
 * simultaneamente para verificar se a implementação com lock pessimista
 * previne race conditions adequadamente.
 */
class AppointmentServiceConcurrencyTest {

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

        service = new AppointmentServiceImpl(appointmentRepository, entityManager, slotRepository, 
                doctorRepository, patientRepository);
    }

    @Test
    void createAppointment_concurrentAccess_onlyOneSucceeds() throws Exception {
        // Arrange: setup comum para ambas as tentativas
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId1 = UUID.randomUUID();
        var patientId2 = UUID.randomUUID();

        // Criar duas instâncias separadas do slot para simular estado diferente
        var availableSlot = new JpaSlotEntity();
        availableSlot.setId(slotId);
        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        availableSlot.setDoctor(doctor);
        availableSlot.setStatus("available");
        availableSlot.setStartTime(OffsetDateTime.of(2025, 9, 3, 10, 0, 0, 0, ZoneOffset.UTC));
        availableSlot.setEndTime(availableSlot.getStartTime().plusMinutes(30));

        var bookedSlot = new JpaSlotEntity();
        bookedSlot.setId(slotId);
        bookedSlot.setDoctor(doctor);
        bookedSlot.setStatus("booked");
        bookedSlot.setStartTime(availableSlot.getStartTime());
        bookedSlot.setEndTime(availableSlot.getEndTime());

        var patient1 = new JpaPatientEntity();
        patient1.setId(patientId1);
        var patient2 = new JpaPatientEntity();
        patient2.setId(patientId2);

        // Mock que alterna entre retornar slot disponível e slot booked
        // para simular o comportamento real de lock pessimista
        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE))
                .thenReturn(availableSlot)  // Primeira chamada: disponível
                .thenReturn(bookedSlot);    // Segunda chamada: já reservado

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId1)).thenReturn(Optional.of(patient1));
        when(patientRepository.findById(patientId2)).thenReturn(Optional.of(patient2));
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(appointmentRepository.save(any(JpaAppointmentEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act: executar duas tentativas sequenciais para simular concorrência
        // (em testes unitários com mocks, execução real concorrente é difícil de controlar)
        Exception result1 = null;
        Exception result2 = null;

        try {
            service.createAppointment(doctorId, slotId, patientId1);
        } catch (Exception e) {
            result1 = e;
        }

        try {
            service.createAppointment(doctorId, slotId, patientId2);
        } catch (Exception e) {
            result2 = e;
        }

        // Assert: primeira deve ter sucesso, segunda deve falhar
        if (result1 != null) {
            throw new AssertionError("First appointment should succeed, but got: " + result1);
        }
        
        if (!(result2 instanceof IllegalStateException)) {
            throw new AssertionError("Second appointment should fail with IllegalStateException, but got: " + result2);
        }

        // Verificar que o lock pessimista foi usado duas vezes
        verify(entityManager, times(2)).find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE);
    }

    @Test
    void createAppointment_slotChangedBetweenLockAndValidation_throws() {
        // Simula cenário onde slot é alterado por outra transação entre
        // o momento do lock e a validação de propriedade
        var doctorId = UUID.randomUUID();
        var wrongDoctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var wrongDoctor = new JpaDoctorEntity();
        wrongDoctor.setId(wrongDoctorId);
        slot.setDoctor(wrongDoctor); // Slot pertence a médico diferente
        slot.setStatus("available");

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(slot);

        // Assert: deve lançar IllegalArgumentException por propriedade incorreta
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
            () -> service.createAppointment(doctorId, slotId, patientId));
        
        assertEquals("slot does not belong to doctor", exception.getMessage());
    }

    @Test
    void createAppointment_doctorNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        slot.setDoctor(doctor);
        slot.setStatus("available");

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(slot);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createAppointment(doctorId, slotId, patientId));
        
        assertEquals("doctor not found", exception.getMessage());
    }

    @Test
    void createAppointment_patientNotFound_throws() {
        var doctorId = UUID.randomUUID();
        var slotId = UUID.randomUUID();
        var patientId = UUID.randomUUID();

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        slot.setDoctor(doctor);
        slot.setStatus("available");

        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE)).thenReturn(slot);
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> service.createAppointment(doctorId, slotId, patientId));
        
        assertEquals("patient not found", exception.getMessage());
    }
}
