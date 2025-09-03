package com.me.medical.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.infra.DoctorRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoint público (read-only) para listar médicos existentes.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Médicos", description = "Consulta de informações dos médicos")
public class DoctorController {
    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    /**
     * Dados básicos de um médico para listagem pública.
     */
    public static class DoctorDto {
        public UUID id;
        public String name;
        public String specialty;
    }

    /**
     * Retorna a lista de todos os médicos cadastrados no sistema.
     * Endpoint público.
     */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorDto>> list() {
        var list = doctorRepository.findAll().stream().map(e -> {
            var d = new DoctorDto();
            d.id = e.getId();
            d.name = e.getName();
            d.specialty = e.getSpecialty();
            return d;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    /**
     * Retorna os dados do médico autenticado atualmente.
     * Apenas médicos podem acessar.
     */
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/doctors/me")
    public ResponseEntity<DoctorDto> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        // somente médicos possuem doctor record
        boolean isDoctor = false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_DOCTOR".equals(ga.getAuthority())) { isDoctor = true; break; }
        }
        if (!isDoctor) return ResponseEntity.status(403).build();

        UUID userId;
        try { userId = UUID.fromString((String) auth.getPrincipal()); }
        catch (Exception e) { return ResponseEntity.status(401).build(); }

        var list = doctorRepository.findByUserId(userId);
        if (list.isEmpty()) return ResponseEntity.status(404).build();
        var e = list.get(0);
        var d = new DoctorDto();
        d.id = e.getId();
        d.name = e.getName();
        d.specialty = e.getSpecialty();
        return ResponseEntity.ok(d);
    }
}
