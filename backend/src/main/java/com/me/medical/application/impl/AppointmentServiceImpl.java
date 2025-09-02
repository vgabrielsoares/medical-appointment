package com.me.medical.application.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.medical.application.AppointmentService;
import com.me.medical.application.dto.AppointmentDto;
import com.me.medical.infra.AppointmentRepository;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaAppointmentEntity;
import com.me.medical.infra.JpaSlotEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.SlotRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

/**
 * Service responsável por criar e listar agendamentos.
 *
 * A criação usa lock pessimista no slot (SELECT FOR UPDATE) para evitar race conditions
 * quando múltiplos pacientes tentam reservar o mesmo slot.
 */
@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final EntityManager entityManager;
    private final SlotRepository slotRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
                                  EntityManager entityManager,
                                  SlotRepository slotRepository,
                                  DoctorRepository doctorRepository,
                                  PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.entityManager = entityManager;
        this.slotRepository = slotRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    @Transactional
    /**
     * Cria um agendamento (reserva) para o slot informado.
     *
     * Implementação transacional que utiliza lock pessimista no registro do slot
     * (SELECT FOR UPDATE) para prevenir race conditions entre clientes concorrentes.
     *
     * Pré-condições:
     * - slot existe e pertence ao doctorId
     * - slot.status == 'available'
     * - doctor e patient existem
     *
     * Pós-condições:
     * - slot.status setado para 'booked'
     * - appointment persistido com status 'confirmed'
     *
     * @throws IllegalArgumentException quando recursos não existem ou não pertencem
     * @throws IllegalStateException quando slot não está disponível
     */
    public AppointmentDto createAppointment(UUID doctorId, UUID slotId, UUID patientId) {
        // busca slot com lock pessimista para prevenir reservas concorrentes
        var slot = entityManager.find(JpaSlotEntity.class, slotId, LockModeType.PESSIMISTIC_WRITE);
        if (slot == null) throw new IllegalArgumentException("slot not found");

        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new IllegalArgumentException("slot does not belong to doctor");
        }

        if (!"available".equals(slot.getStatus())) {
            throw new IllegalStateException("slot not available");
        }

        var doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("doctor not found"));
        var patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("patient not found"));

        var appointment = new JpaAppointmentEntity();
        appointment.setId(UUID.randomUUID());
        appointment.setSlot(slot);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus("confirmed");
        appointment.setCreatedAt(OffsetDateTime.now());

        // marca slot como booked antes de salvar o appointment para garantir visibilidade
        slot.setStatus("booked");
        slotRepository.save(slot);

        var saved = appointmentRepository.save(appointment);
        return toDto(saved);
    }

    @Override
    public List<AppointmentDto> listByPatient(UUID patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    private AppointmentDto toDto(JpaAppointmentEntity e) {
        var d = new AppointmentDto();
        d.setId(e.getId());
        d.setSlotId(e.getSlot() != null ? e.getSlot().getId() : null);
        d.setDoctorId(e.getDoctor() != null ? e.getDoctor().getId() : null);
        d.setPatientId(e.getPatient() != null ? e.getPatient().getId() : null);
        d.setStatus(e.getStatus());
        d.setCreatedAt(e.getCreatedAt());
        
        // Enriquecer com dados do médico
        if (e.getDoctor() != null) {
            d.setDoctorName(e.getDoctor().getName());
            d.setDoctorSpecialty(e.getDoctor().getSpecialty());
        }
        
        // Enriquecer com dados do slot (horários)
        if (e.getSlot() != null) {
            d.setStart(e.getSlot().getStartTime());
            d.setEnd(e.getSlot().getEndTime());
        }
        
        return d;
    }
}
