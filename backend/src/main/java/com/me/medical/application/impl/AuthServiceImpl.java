package com.me.medical.application.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.me.medical.application.AuthService;
import com.me.medical.config.JwtTokenProvider;
import com.me.medical.infra.JpaUserEntity;
import com.me.medical.infra.UserRepository;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String login(String email, String password) {
        Optional<JpaUserEntity> userOpt = userRepository.findByEmail(email);
        var user = userOpt.orElseThrow(() -> new RuntimeException("Credenciais inválidas"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String role = user.getRole() != null ? user.getRole().getName() : "ROLE_PATIENT";
        return tokenProvider.createToken(user.getId().toString(), role);
    }
}
