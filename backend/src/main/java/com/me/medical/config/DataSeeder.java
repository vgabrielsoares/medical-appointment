package com.me.medical.config;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaRoleEntity;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.RoleRepository;
import com.me.medical.infra.UserRepository;

@Configuration
public class DataSeeder {
    @Bean
    public CommandLineRunner seed(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder,
            DoctorRepository doctorRepository, PatientRepository patientRepository) {
        return args -> {
            var roleDoctor = roleRepository.findByName("ROLE_DOCTOR").orElseGet(() -> {
                var r = new JpaRoleEntity();
                r.setId(UUID.randomUUID());
                r.setName("ROLE_DOCTOR");
                r.setCreatedAt(OffsetDateTime.now());
                return roleRepository.save(r);
            });

            var rolePatient = roleRepository.findByName("ROLE_PATIENT").orElseGet(() -> {
                var r = new JpaRoleEntity();
                r.setId(UUID.randomUUID());
                r.setName("ROLE_PATIENT");
                r.setCreatedAt(OffsetDateTime.now());
                return roleRepository.save(r);
            });

            if (userRepository.findByEmail("doctor@example.com").isEmpty()) {
                var u = new JpaUserEntity();
                u.setId(UUID.randomUUID());
                u.setEmail("doctor@example.com");
                u.setPasswordHash(passwordEncoder.encode("doctorpass"));
                u.setCreatedAt(OffsetDateTime.now());
                u.setRole(roleDoctor);
                userRepository.save(u);
            }

            if (userRepository.findByEmail("patient@example.com").isEmpty()) {
                var u = new JpaUserEntity();
                u.setId(UUID.randomUUID());
                u.setEmail("patient@example.com");
                u.setPasswordHash(passwordEncoder.encode("patientpass"));
                u.setCreatedAt(OffsetDateTime.now());
                u.setRole(rolePatient);
                userRepository.save(u);
            }

            // Garantir que existam registros em doctors e patients ligados aos usuários seed
            var doctorUser = userRepository.findByEmail("doctor@example.com").orElseThrow();
            if (doctorRepository.findByUserId(doctorUser.getId()).isEmpty()) {
                var d = new JpaDoctorEntity();
                d.setId(UUID.randomUUID());
                d.setUser(doctorUser);
                d.setName("Dr. Example");
                d.setSpecialty("General");
                d.setCreatedAt(OffsetDateTime.now());
                doctorRepository.save(d);
            }

            var patientUser = userRepository.findByEmail("patient@example.com").orElseThrow();
            if (patientRepository.findByUserId(patientUser.getId()).isEmpty()) {
                var p = new JpaPatientEntity();
                p.setId(UUID.randomUUID());
                p.setUser(patientUser);
                p.setName("Patient Example");
                p.setCreatedAt(OffsetDateTime.now());
                patientRepository.save(p);
            }
        };
    }
}
