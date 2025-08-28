package com.me.medical.api;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.me.medical.application.AppointmentService;
import com.me.medical.application.dto.AppointmentDto;
import com.me.medical.application.dto.CreateAppointmentRequest;

/**
 * Controller para criação de agendamentos e listagem de agendamentos do paciente.
 */
@RestController
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    private UUID authUserId(Authentication auth) {
        if (auth == null) return null;
        try { return UUID.fromString((String) auth.getPrincipal()); }
        catch (Exception e) { return null; }
    }

    private boolean isPatient(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_PATIENT".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    @PostMapping("/api/appointments")
    public ResponseEntity<?> create(@RequestBody CreateAppointmentRequest req, Authentication auth) {
        // paciente apenas pode criar agendamento para si mesmo (patientId do token)
        if (!isPatient(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requires ROLE_PATIENT");
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");

        try {
            var created = appointmentService.createAppointment(req.getDoctorId(), req.getSlotId(), userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/api/patients/{patientId}/appointments")
    public ResponseEntity<List<AppointmentDto>> listByPatient(@PathVariable UUID patientId, Authentication auth) {
        // pacientes só podem listar seus próprios agendamentos
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");
        if (!userId.equals(patientId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "can only list own appointments");

        var list = appointmentService.listByPatient(patientId);
        return ResponseEntity.ok(list);
    }
}
