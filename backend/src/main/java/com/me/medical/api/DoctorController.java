package com.me.medical.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.infra.DoctorRepository;

/**
 * Endpoint público (read-only) para listar médicos existentes.
 */
@RestController
@RequestMapping("/api")
public class DoctorController {
    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    public static class DoctorDto {
        public UUID id;
        public String name;
        public String specialty;
    }

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
}
