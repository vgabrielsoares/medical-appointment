package com.me.medical.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.me.medical.application.HealthUseCase;
import com.me.medical.application.impl.HealthUseCaseImpl;

@Configuration
public class ApplicationConfig {

    @Bean
    public HealthUseCase healthUseCase() {
        return new HealthUseCaseImpl();
    }
}
