package com.me.medical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.me.medical.application.HealthUseCase;
import com.me.medical.application.impl.HealthUseCaseImpl;

@Configuration
public class ApplicationConfig {

    @Bean
    public HealthUseCase healthUseCase() {
        return new HealthUseCaseImpl();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtProperties props) {
        return new JwtTokenProvider(props);
    }
}
