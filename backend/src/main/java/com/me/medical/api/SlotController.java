package com.me.medical.api;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.me.medical.application.SlotOverlapException;
import com.me.medical.application.SlotService;
import com.me.medical.application.dto.SlotDto;
import com.me.medical.infra.DoctorRepository;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller REST para CRUD de slots. Somente médicos podem gerenciar seus slots.
 */
@RestController
@RequestMapping("/api/doctors/{doctorId}/slots")
@Tag(name = "Slots", description = "Gerenciamento de horários disponíveis dos médicos")
@SecurityRequirement(name = "bearerAuth")
public class SlotController {
    private final SlotService slotService;
    private final DoctorRepository doctorRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SlotController.class);

    public SlotController(SlotService slotService, DoctorRepository doctorRepository) {
        this.slotService = slotService;
        this.doctorRepository = doctorRepository;
    }

    /**
     * Cria um novo slot/horário disponível para o médico.
     * Apenas o próprio médico pode criar seus slots.
     */
    @PostMapping
    public ResponseEntity<?> create(@PathVariable UUID doctorId, @RequestBody SlotDto dto, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        try {
            var created = slotService.createSlot(doctorId, dto);
            return ResponseEntity.status(201).body(created);
        } catch (SlotOverlapException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Lista os slots do médico.
     * Médicos proprietários veem todos os slots, outros usuários veem apenas slots disponíveis.
     */
    @GetMapping
    public ResponseEntity<List<SlotDto>> list(@PathVariable UUID doctorId, Authentication auth) {
        // listagem dos slots do médico é permitida por ele
        // médicos donos veem todos os slots, pacientes e público veem somente slots disponíveis
        if (AuthUtils.isDoctor(auth)) {
            var userId = AuthUtils.authUserId(auth);
            if (userId != null) {
                var doctor = doctorRepository.findById(doctorId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "doctor not found"));
                var ownerUser = doctor.getUser();
                if (ownerUser != null && ownerUser.getId().equals(userId)) {
                    return ResponseEntity.ok(slotService.listSlots(doctorId));
                }
            }
        }

        var all = slotService.listSlots(doctorId);
        var available = all.stream()
            .filter(s -> s.getStatus() != null && "available".equalsIgnoreCase(s.getStatus()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(available);
    }

    /**
     * Atualiza um slot existente do médico.
     * Apenas o próprio médico pode atualizar seus slots.
     */
    @PutMapping("/{slotId}")
    public ResponseEntity<?> update(@PathVariable UUID doctorId, @PathVariable UUID slotId, @RequestBody SlotDto dto, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        try {
            var updated = slotService.updateSlot(doctorId, slotId, dto);
            return ResponseEntity.ok(updated);
        } catch (SlotOverlapException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    /**
     * Remove um slot do médico.
     * Apenas o próprio médico pode deletar seus slots.
     */
    @DeleteMapping("/{slotId}")
    public ResponseEntity<?> delete(@PathVariable UUID doctorId, @PathVariable UUID slotId, Authentication auth) {
        requireDoctorAndOwner(auth, doctorId);
        slotService.deleteSlot(doctorId, slotId);
        return ResponseEntity.noContent().build();
    }

    private void requireDoctorAndOwner(Authentication auth, UUID doctorId) {
        if (!AuthUtils.isDoctor(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requires ROLE_DOCTOR");
        var userId = AuthUtils.authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");

        var doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "doctor not found"));

        var ownerUser = doctor.getUser();
        if (ownerUser == null || !ownerUser.getId().equals(userId)) {
            // logar detalhes do motivo para facilitar debugging
            log.debug("requireDoctorAndOwner failed: authUserId={} doctorId={} ownerUserId={} ownerUserIsNull={}",
                userId, doctorId, ownerUser != null ? ownerUser.getId() : null, ownerUser == null);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "not owner");
        }
    }
}
