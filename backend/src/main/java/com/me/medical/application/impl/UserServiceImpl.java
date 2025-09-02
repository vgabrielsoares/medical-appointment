package com.me.medical.application.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.me.medical.application.UserService;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.UserRepository;

/**
 * Implementação do serviço de usuário responsável por gerenciar perfis.
 */
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    
    public UserServiceImpl(UserRepository userRepository, 
                          DoctorRepository doctorRepository, 
                          PatientRepository patientRepository) {
        this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }
    
    @Override
    /**
     * Retorna os dados do perfil para o usuário identificado pelo email.
     * Agrega informações específicas dependendo da role (doctor/patient).
     *
     * @throws RuntimeException se o usuário não for encontrado
     */
    public Map<String, Object> getProfile(String userEmail) {
        Optional<JpaUserEntity> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado: " + userEmail);
        }
        
        JpaUserEntity user = userOpt.get();
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("email", user.getEmail());
        profile.put("role", user.getRole().getName());
        
        // Buscar informações específicas do perfil baseado no role
        String roleName = user.getRole().getName();
        
        if ("ROLE_DOCTOR".equals(roleName)) {
            List<JpaDoctorEntity> doctors = doctorRepository.findByUserId(user.getId());
            if (!doctors.isEmpty()) {
                JpaDoctorEntity doctor = doctors.get(0);
                profile.put("name", doctor.getName());
                profile.put("specialty", doctor.getSpecialty());
                profile.put("phone", doctor.getPhone());
                profile.put("birthDate", doctor.getBirthDate() != null ? 
                    doctor.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
            }
        } else if ("ROLE_PATIENT".equals(roleName)) {
            List<JpaPatientEntity> patients = patientRepository.findByUserId(user.getId());
            if (!patients.isEmpty()) {
                JpaPatientEntity patient = patients.get(0);
                profile.put("name", patient.getName());
                profile.put("phone", patient.getPhone());
                profile.put("birthDate", patient.getBirthDate() != null ? 
                    patient.getBirthDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
            }
        }
        
        return profile;
    }
    
    @Override
    @Transactional
    /**
     * Atualiza parcialmente os dados do perfil do usuário autenticado.
     * Aceita chaves 'name', 'phone', 'specialty' (para doctor) e 'birthDate'.
     *
     * Retorna o perfil atualizado.
     *
     * @throws RuntimeException se o usuário não existir
     */
    public Map<String, Object> updateProfile(String userEmail, Map<String, Object> profileData) {
        Optional<JpaUserEntity> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado: " + userEmail);
        }
        
        JpaUserEntity user = userOpt.get();
        String roleName = user.getRole().getName();
        
        if ("ROLE_DOCTOR".equals(roleName)) {
            List<JpaDoctorEntity> doctors = doctorRepository.findByUserId(user.getId());
            if (!doctors.isEmpty()) {
                JpaDoctorEntity doctor = doctors.get(0);
                updateDoctorProfile(doctor, profileData);
                doctorRepository.save(doctor);
            }
        } else if ("ROLE_PATIENT".equals(roleName)) {
            List<JpaPatientEntity> patients = patientRepository.findByUserId(user.getId());
            if (!patients.isEmpty()) {
                JpaPatientEntity patient = patients.get(0);
                updatePatientProfile(patient, profileData);
                patientRepository.save(patient);
            }
        }
        
        return getProfile(userEmail);
    }
    
    private void updateDoctorProfile(JpaDoctorEntity doctor, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            doctor.setName((String) profileData.get("name"));
        }
        
        if (profileData.containsKey("phone")) {
            doctor.setPhone((String) profileData.get("phone"));
        }
        
        if (profileData.containsKey("specialty")) {
            doctor.setSpecialty((String) profileData.get("specialty"));
        }
        
        updateBirthDate(profileData, (birthDate) -> doctor.setBirthDate(birthDate));
    }
    
    private void updatePatientProfile(JpaPatientEntity patient, Map<String, Object> profileData) {
        if (profileData.containsKey("name")) {
            patient.setName((String) profileData.get("name"));
        }
        
        if (profileData.containsKey("phone")) {
            patient.setPhone((String) profileData.get("phone"));
        }
        
        updateBirthDate(profileData, (birthDate) -> patient.setBirthDate(birthDate));
    }
    
    private void updateBirthDate(Map<String, Object> profileData, java.util.function.Consumer<LocalDate> setter) {
        if (profileData.containsKey("birthDate") && profileData.get("birthDate") != null) {
            String birthDateStr = (String) profileData.get("birthDate");
            if (!birthDateStr.trim().isEmpty()) {
                // Aceitar formato ISO (YYYY-MM-DD) ou datetime-local (YYYY-MM-DDTHH:mm)
                if (birthDateStr.contains("T")) {
                    birthDateStr = birthDateStr.split("T")[0];
                }
                LocalDate birthDate = LocalDate.parse(birthDateStr);
                setter.accept(birthDate);
            }
        }
    }
}
