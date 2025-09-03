package com.me.medical.application.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.me.medical.config.JwtTokenProvider;
import com.me.medical.infra.DoctorRepository;
import com.me.medical.infra.JpaDoctorEntity;
import com.me.medical.infra.JpaPatientEntity;
import com.me.medical.infra.JpaRoleEntity;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.PatientRepository;
import com.me.medical.infra.RoleRepository;
import com.me.medical.infra.UserRepository;

/**
 * Testes unitários para AuthServiceImpl.
 * 
 * Cobre os cenários críticos de autenticação:
 * - Login com credenciais válidas/inválidas
 * - Geração e estrutura do token JWT
 * - Busca e montagem dos dados do usuário por role
 * - Tratamento de erros de autenticação
 */
class AuthServiceImplTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private RoleRepository roleRepository;
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        tokenProvider = mock(JwtTokenProvider.class);
        doctorRepository = mock(DoctorRepository.class);
        patientRepository = mock(PatientRepository.class);
        roleRepository = mock(RoleRepository.class);

        authService = new AuthServiceImpl(userRepository, passwordEncoder, tokenProvider,
                doctorRepository, patientRepository, roleRepository);
    }

    @Test
    void login_validCredentials_returnsTokenAndUserInfo() {
        // Arrange
        var email = "doctor@example.com";
        var password = "doctorpass";
        var hashedPassword = "$2a$10$hashedpassword";
        var userId = UUID.randomUUID();
        var doctorId = UUID.randomUUID();
        var token = "jwt.token.here";

        var role = new JpaRoleEntity();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_DOCTOR");

        var user = new JpaUserEntity();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(role);
        user.setCreatedAt(OffsetDateTime.now());

        var doctor = new JpaDoctorEntity();
        doctor.setId(doctorId);
        doctor.setUser(user);
        doctor.setName("Dr. João Silva");
        doctor.setSpecialty("Cardiologia");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenProvider.createToken(userId.toString(), "ROLE_DOCTOR")).thenReturn(token);
        when(doctorRepository.findByUserId(userId)).thenReturn(Collections.singletonList(doctor));

        // Act
        Map<String, Object> result = authService.login(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.get("token"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertNotNull(userInfo);
        assertEquals(userId.toString(), userInfo.get("id"));
        assertEquals("ROLE_DOCTOR", userInfo.get("role"));
        assertEquals("Dr. João Silva", userInfo.get("name"));
        assertEquals(email, userInfo.get("email"));

        // Verify interactions
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, hashedPassword);
        verify(tokenProvider).createToken(userId.toString(), "ROLE_DOCTOR");
        verify(doctorRepository).findByUserId(userId);
    }

    @Test
    void login_patientCredentials_returnsCorrectUserInfo() {
        // Arrange
        var email = "patient@example.com";
        var password = "patientpass";
        var hashedPassword = "$2a$10$hashedpatientpassword";
        var userId = UUID.randomUUID();
        var patientId = UUID.randomUUID();
        var token = "patient.jwt.token";

        var role = new JpaRoleEntity();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_PATIENT");

        var user = new JpaUserEntity();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(role);

        var patient = new JpaPatientEntity();
        patient.setId(patientId);
        patient.setUser(user);
        patient.setName("Maria Santos");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenProvider.createToken(userId.toString(), "ROLE_PATIENT")).thenReturn(token);
        when(doctorRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(patientRepository.findByUserId(userId)).thenReturn(Collections.singletonList(patient));

        // Act
        Map<String, Object> result = authService.login(email, password);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertEquals("ROLE_PATIENT", userInfo.get("role"));
        assertEquals("Maria Santos", userInfo.get("name"));

        verify(patientRepository).findByUserId(userId);
    }

    @Test
    void login_emailNotFound_throwsException() {
        // Arrange
        var email = "notfound@example.com";
        var password = "anypassword";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(email, password));
        
        assertEquals("Credenciais inválidas", exception.getMessage());

        verify(userRepository).findByEmail(email);
        // Não deve tentar verificar senha se usuário não existe
        verify(passwordEncoder, org.mockito.Mockito.never()).matches(anyString(), anyString());
    }

    @Test
    void login_wrongPassword_throwsException() {
        // Arrange
        var email = "doctor@example.com";
        var wrongPassword = "wrongpassword";
        var correctHashedPassword = "$2a$10$correcthash";

        var user = new JpaUserEntity();
        user.setEmail(email);
        user.setPasswordHash(correctHashedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(wrongPassword, correctHashedPassword)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(email, wrongPassword));
        
        assertEquals("Credenciais inválidas", exception.getMessage());

        verify(passwordEncoder).matches(wrongPassword, correctHashedPassword);
        // Não deve tentar gerar token se senha está errada
        verify(tokenProvider, org.mockito.Mockito.never()).createToken(anyString(), anyString());
    }

    @Test
    void login_userWithoutSpecificProfile_usesEmailAsName() {
        // Arrange: usuário existe mas não tem perfil específico (doctor/patient)
        var email = "orphan@example.com";
        var password = "orphanpass";
        var hashedPassword = "$2a$10$orphanhash";
        var userId = UUID.randomUUID();
        var token = "orphan.token";

        var role = new JpaRoleEntity();
        role.setId(UUID.randomUUID());
        role.setName("ROLE_PATIENT");

        var user = new JpaUserEntity();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash(hashedPassword);
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, hashedPassword)).thenReturn(true);
        when(tokenProvider.createToken(userId.toString(), "ROLE_PATIENT")).thenReturn(token);
        when(doctorRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(patientRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> result = authService.login(email, password);

        // Assert
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertEquals(email, userInfo.get("name")); // deve usar email como fallback
    }

    @Test
    void login_roleWithoutRolePrefix_normalizesRole() {
        // Arrange: role no banco sem prefixo "ROLE_"
        var email = "test@example.com";
        var password = "testpass";
        var userId = UUID.randomUUID();

        var role = new JpaRoleEntity();
        role.setName("DOCTOR"); // sem prefixo ROLE_

        var user = new JpaUserEntity();
        user.setId(userId);
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setRole(role);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hash")).thenReturn(true);
        when(tokenProvider.createToken(userId.toString(), "ROLE_DOCTOR")).thenReturn("token");
        when(doctorRepository.findByUserId(userId)).thenReturn(Collections.emptyList());

        // Act
        authService.login(email, password);

        // Assert
        verify(tokenProvider).createToken(userId.toString(), "ROLE_DOCTOR");
    }

    @Test
    void emailExists_existingEmail_returnsTrue() {
        // Arrange
        var email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new JpaUserEntity()));

        // Act
        boolean exists = authService.emailExists(email);

        // Assert
        assertTrue(exists);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void emailExists_nonExistingEmail_returnsFalse() {
        // Arrange
        var email = "nonexisting@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean exists = authService.emailExists(email);

        // Assert
        assertTrue(!exists);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void register_validDoctorData_createsUserAndProfile() {
        // Arrange
        var name = "Dr. Carlos Silva";
        var email = "carlos@example.com";
        var password = "carlossecret";
        var specialty = "Neurologia";
        var phone = "11999999999";
        var role = "DOCTOR";
        var hashedPassword = "$2a$10$hashed";
        var token = "registration.token";

        var roleEntity = new JpaRoleEntity();
        roleEntity.setId(UUID.randomUUID());
        roleEntity.setName("ROLE_DOCTOR");

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_DOCTOR")).thenReturn(Optional.of(roleEntity));
        when(passwordEncoder.encode(password)).thenReturn(hashedPassword);
        when(userRepository.save(any(JpaUserEntity.class))).thenAnswer(i -> {
            JpaUserEntity user = i.getArgument(0);
            user.setId(UUID.randomUUID()); // simular ID gerado
            return user;
        });
        when(doctorRepository.save(any(JpaDoctorEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(tokenProvider.createToken(anyString(), anyString())).thenReturn(token);

        // Act
        Map<String, Object> result = authService.register(name, email, password, role, specialty, phone);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.get("token"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> userInfo = (Map<String, Object>) result.get("user");
        assertEquals("ROLE_DOCTOR", userInfo.get("role"));
        assertEquals(name, userInfo.get("name"));
        assertEquals(email, userInfo.get("email"));

        verify(userRepository).save(any(JpaUserEntity.class));
        verify(doctorRepository).save(any(JpaDoctorEntity.class));
    }

    @Test
    void register_existingEmail_throwsException() {
        // Arrange
        var email = "existing@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new JpaUserEntity()));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register("Name", email, "pass", "DOCTOR", "Spec", "Phone"));
        
        assertEquals("Email já está em uso", exception.getMessage());

        // Não deve tentar criar usuário se email já existe
        verify(userRepository, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void register_invalidRole_throwsException() {
        // Arrange
        var email = "test@example.com";
        var invalidRole = "INVALID_ROLE";
        
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_INVALID_ROLE")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register("Name", email, "pass", invalidRole, "Spec", "Phone"));
        
        assertTrue(exception.getMessage().contains("Role inválida"));
    }
}
