package com.me.medical.application.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.me.medical.application.AuthService;
import com.me.medical.config.JwtTokenProvider;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                          JwtTokenProvider tokenProvider, DoctorRepository doctorRepository,
                          PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public Map<String, Object> login(String email, String password) {
        Optional<JpaUserEntity> userOpt = userRepository.findByEmail(email);
        var user = userOpt.orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        // Normaliza a role para o formato esperado pelo Spring Security (ex: ROLE_PATIENT)
        String roleRaw = user.getRole() != null ? user.getRole().getName() : "PATIENT";
        String role = roleRaw.startsWith("ROLE_") ? roleRaw : ("ROLE_" + roleRaw);
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
}
