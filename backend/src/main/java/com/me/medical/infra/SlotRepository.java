package com.me.medical.infra;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SlotRepository extends JpaRepository<JpaSlotEntity, UUID> {
    List<JpaSlotEntity> findByDoctorId(UUID doctorId);

    // Retorna slots do médico que se sobrepõem ao intervalo [start, end)
    @Query("SELECT s FROM JpaSlotEntity s WHERE s.doctor.id = :doctorId AND NOT (s.endTime <= :start OR s.startTime >= :end)")
    List<JpaSlotEntity> findOverlappingSlots(@Param("doctorId") UUID doctorId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end);

    // Mesma query, mas exclui um slot específico (útil para updates)
    @Query("SELECT s FROM JpaSlotEntity s WHERE s.doctor.id = :doctorId AND s.id <> :excludeId AND NOT (s.endTime <= :start OR s.startTime >= :end)")
    List<JpaSlotEntity> findOverlappingSlotsExcludingId(@Param("doctorId") UUID doctorId, @Param("start") OffsetDateTime start, @Param("end") OffsetDateTime end, @Param("excludeId") UUID excludeId);
}
