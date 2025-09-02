package com.me.medical.application.impl;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.me.medical.application.AuthService;
import com.me.medical.config.JwtTokenProvider;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.RoleRepository;
import com.me.medical.infra.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;

        public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider, DoctorRepository doctorRepository,
            PatientRepository patientRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Map<String, Object> login(String email, String password) {
        Optional<JpaUserEntity> userOpt = userRepository.findByEmail(email);
        var user = userOpt.orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Normaliza a role para o formato esperado pelo Spring Security (ex:
        // ROLE_PATIENT)
        String roleRaw = user.getRole() != null ? user.getRole().getName() : "PATIENT";
        String role = roleRaw.startsWith("ROLE_") ? roleRaw : ("ROLE_" + roleRaw);
        // Usar id do usuário (UUID) como 'sub' no JWT para que o principal
        // disponível no SecurityContext seja o ID (compatível com controllers que
        // fazem UUID.fromString(auth.getPrincipal())).
        String token = tokenProvider.createToken(user.getId().toString(), role);

        // Buscar o nome do usuário baseado na role
        String name = user.getEmail(); // fallback para email se não encontrar nome
        if ("ROLE_DOCTOR".equals(role)) {
            var doctors = doctorRepository.findByUserId(user.getId());
            if (!doctors.isEmpty()) {
                name = doctors.get(0).getName();
            }
        } else if ("ROLE_PATIENT".equals(role)) {
            var patients = patientRepository.findByUserId(user.getId());
            if (!patients.isEmpty()) {
                name = patients.get(0).getName();
            }
        }

        // Montar resposta com token e dados do usuário
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("role", role);
        userInfo.put("name", name);
        userInfo.put("email", user.getEmail());
        response.put("user", userInfo);

        return response;
    }

    @Override
    public Map<String, Object> register(String name, String email, String password, String role, String specialty,
            String phone) {
        // Verificar se o email já está em uso
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email já está em uso");
        }

        // Normalizar role
        String normalizedRole = role.startsWith("ROLE_") ? role : ("ROLE_" + role);

        // Buscar role no banco
        var roleEntity = roleRepository.findByName(normalizedRole)
                .orElseThrow(() -> new RuntimeException("Role inválida: " + normalizedRole));

        // Criar usuário
        JpaUserEntity user = new JpaUserEntity();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(roleEntity);
        user.setCreatedAt(OffsetDateTime.now());

        user = userRepository.save(user);

        // Criar perfil específico baseado na role
        if ("ROLE_DOCTOR".equals(normalizedRole)) {
            JpaDoctorEntity doctor = new JpaDoctorEntity();
            doctor.setId(UUID.randomUUID());
            doctor.setUser(user);
            doctor.setName(name);
            doctor.setSpecialty(specialty != null ? specialty : "");
            doctor.setCreatedAt(OffsetDateTime.now());
            doctorRepository.save(doctor);
        } else if ("ROLE_PATIENT".equals(normalizedRole)) {
            JpaPatientEntity patient = new JpaPatientEntity();
            patient.setId(UUID.randomUUID());
            patient.setUser(user);
            patient.setName(name);
            patient.setCreatedAt(OffsetDateTime.now());
            patientRepository.save(patient);
        }

        // Gerar token e retornar dados (login automático após registro)
        // Usar id do usuário (UUID) como 'sub' no JWT para consistência com os
        // controllers que esperam o ID no Authentication.getPrincipal().
        String token = tokenProvider.createToken(user.getId().toString(), normalizedRole);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId().toString());
        userInfo.put("role", normalizedRole);
        userInfo.put("name", name);
        userInfo.put("email", email);
        response.put("user", userInfo);

        return response;
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
