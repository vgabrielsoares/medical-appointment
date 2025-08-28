package com.me.medical.infra;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<JpaDoctorEntity, UUID> {
    List<JpaDoctorEntity> findByUserId(UUID userId);
}
