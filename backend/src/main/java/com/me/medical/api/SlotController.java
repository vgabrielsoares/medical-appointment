package com.me.medical.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.me.medical.application.SlotService;
import com.me.medical.application.dto.SlotDto;
import com.me.medical.infra.DoctorRepository;

/**
 * Controller REST para CRUD de slots. Somente médicos podem gerenciar seus slots.
 */
@RestController
@RequestMapping("/api/doctors/{doctorId}/slots")
public class SlotController {
    private final SlotService slotService;

    private final DoctorRepository doctorRepository;

    public SlotController(SlotService slotService, DoctorRepository doctorRepository) {
        this.slotService = slotService;
        this.doctorRepository = doctorRepository;
    }

    private UUID authUserId(Authentication auth) {
        if (auth == null) return null;
        try { return UUID.fromString((String) auth.getPrincipal()); }
        catch (Exception e) { return null; }
    }

    private boolean isDoctor(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_DOCTOR".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    @PostMapping
    public ResponseEntity<?> create(@PathVariable UUID doctorId, @RequestBody SlotDto dto, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        var created = slotService.createSlot(doctorId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SlotDto>> list(@PathVariable UUID doctorId, Authentication auth) {
        // listagem dos slots do médico é permitida por ele
        requireDoctorAndOwner(auth, doctorId);
        return ResponseEntity.ok(slotService.listSlots(doctorId));
    }

    @PutMapping("/{slotId}")
    public ResponseEntity<?> update(@PathVariable UUID doctorId, @PathVariable UUID slotId, @RequestBody SlotDto dto, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        var updated = slotService.updateSlot(doctorId, slotId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> delete(@PathVariable UUID doctorId, @PathVariable UUID slotId, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        slotService.deleteSlot(doctorId, slotId);
        return ResponseEntity.noContent().build();
    }

    private void requireDoctorAndOwner(Authentication auth, UUID doctorId) {
        if (!isDoctor(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requires ROLE_DOCTOR");
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");

        var doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "doctor not found"));

        var ownerUser = doctor.getUser();
        if (ownerUser == null || !ownerUser.getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not owner");
        }
    }
}
