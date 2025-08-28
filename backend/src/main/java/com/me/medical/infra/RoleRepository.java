package com.me.medical.infra;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<JpaRoleEntity, UUID> {
    Optional<JpaRoleEntity> findByName(String name);
}
