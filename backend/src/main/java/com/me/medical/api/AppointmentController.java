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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.me.medical.application.AppointmentService;
import com.me.medical.application.dto.AppointmentDto;
import com.me.medical.application.dto.CreateAppointmentRequest;
import com.me.medical.infra.PatientRepository;

/**
 * Controller para criação de agendamentos e listagem de agendamentos do paciente.
 */
@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final PatientRepository patientRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AppointmentController.class);

    public AppointmentController(AppointmentService appointmentService, PatientRepository patientRepository) {
        this.appointmentService = appointmentService;
        this.patientRepository = patientRepository;
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

    @PostMapping("/appointments")
    public ResponseEntity<?> create(@RequestBody CreateAppointmentRequest req, Authentication auth) {
        // paciente apenas pode criar agendamento para si mesmo (patientId do token)
        log.debug("Create appointment request received: doctorId={} slotId={} authPresent={}", req.getDoctorId(), req.getSlotId(), auth != null);
        if (!isPatient(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requires ROLE_PATIENT");
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");

        // converter userId (usuário autenticado) para patientId (entidade paciente)
        var patients = patientRepository.findByUserId(userId);
        if (patients.isEmpty()) {
            log.debug("No patient record found for user {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "patient not found");
        }
        var patient = patients.get(0);

        try {
            var created = appointmentService.createAppointment(req.getDoctorId(), req.getSlotId(), patient.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.debug("Appointment create failed (bad request): {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            log.debug("Appointment create failed (conflict): {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            // log completo para diagnóstico, mantemos mensagem genérica ao cliente
            log.error("Unexpected error creating appointment: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "internal error");
        }
    }

    @GetMapping("/appointments/my")
    public ResponseEntity<List<AppointmentDto>> listMyAppointments(Authentication auth) {
        if (!isPatient(auth)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "requires ROLE_PATIENT");
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");

        // converter userId para patientId
        var patients = patientRepository.findByUserId(userId);
        if (patients.isEmpty()) {
            log.debug("No patient record found for user {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "patient not found");
        }
        var patient = patients.get(0);

        var list = appointmentService.listByPatient(patient.getId());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<List<AppointmentDto>> listByPatient(@PathVariable UUID patientId, Authentication auth) {
        // pacientes só podem listar seus próprios agendamentos
        var userId = authUserId(auth);
        if (userId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid principal");
        if (!userId.equals(patientId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "can only list own appointments");

        var list = appointmentService.listByPatient(patientId);
        return ResponseEntity.ok(list);
    }
}
