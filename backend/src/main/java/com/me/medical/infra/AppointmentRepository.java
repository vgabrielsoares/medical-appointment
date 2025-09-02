package com.me.medical.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<JpaAppointmentEntity, UUID> {
    List<JpaAppointmentEntity> findByPatientId(UUID patientId);
}
