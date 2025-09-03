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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

        var slot = new JpaSlotEntity();
        slot.setId(slotId);
        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        slot.setDoctor(doctor);
        slot.setStatus("available");
        slot.setStartTime(OffsetDateTime.of(2025, 9, 3, 10, 0, 0, 0, ZoneOffset.UTC));
        slot.setEndTime(slot.getStartTime().plusMinutes(30));

        var patient1 = new JpaPatientEntity();
        patient1.setId(patientId1);
        var patient2 = new JpaPatientEntity();
        patient2.setId(patientId2);

        // Mock para simular comportamento de lock pessimista onde o primeiro acesso
        // obtém o slot disponível e o segundo obtém o slot já marcado como 'booked'
        var slotAccessCount = new AtomicReference<>(0);
        when(entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE))
                .thenAnswer(new Answer<JpaSlotEntity>() {
                    @Override
                    public JpaSlotEntity answer(InvocationOnMock invocation) throws Throwable {
                        int currentCount = slotAccessCount.updateAndGet(i -> i + 1);
                        if (currentCount == 1) {
                            // Primeira chamada: slot disponível
                            slot.setStatus("available");
                            return slot;
                        } else {
                            // Segunda chamada: slot já foi reservado pela primeira transação
                            slot.setStatus("booked");
                            return slot;
                        }
                    }
                });

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(patientRepository.findById(patientId1)).thenReturn(Optional.of(patient1));
        when(patientRepository.findById(patientId2)).thenReturn(Optional.of(patient2));
        when(slotRepository.save(any(JpaSlotEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(appointmentRepository.save(any(JpaAppointmentEntity.class))).thenAnswer(i -> i.getArgument(0));

        // Act: executar duas tentativas concorrentes de reserva
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(2);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Future<Exception> future1 = executor.submit(() -> {
            try {
                startLatch.await();
                service.createAppointment(doctorId, slotId, patientId1);
                return null;
            } catch (Exception e) {
                return e;
            } finally {
                finishLatch.countDown();
            }
        });

        Future<Exception> future2 = executor.submit(() -> {
            try {
                startLatch.await();
                service.createAppointment(doctorId, slotId, patientId2);
                return null;
            } catch (Exception e) {
                return e;
            } finally {
                finishLatch.countDown();
            }
        });

        // Liberar ambas as threads simultaneamente
        startLatch.countDown();
        finishLatch.await();

        // Assert: uma deve ter sucesso e outra deve falhar
        Exception result1 = future1.get();
        Exception result2 = future2.get();

        // Exatamente uma das operações deve ter sucesso (result == null)
        // e a outra deve ter falhado com IllegalStateException
        boolean oneSuccessOneFailure = (result1 == null && result2 instanceof IllegalStateException) ||
                                     (result2 == null && result1 instanceof IllegalStateException);

        if (!oneSuccessOneFailure) {
            throw new AssertionError(
                String.format("Expected one success and one IllegalStateException, got: result1=%s, result2=%s", 
                             result1, result2));
        }

        // Verificar que o lock pessimista foi chamado para ambas as tentativas
        verify(entityManager, times(2)).find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE);

        executor.shutdown();
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
