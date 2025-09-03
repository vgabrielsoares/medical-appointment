package com.me.medical.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.application.HealthUseCase;

import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Endpoint para verificação de saúde da aplicação.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Verificação de saúde da aplicação")
public class HealthController {

    private final HealthUseCase healthUseCase;

    public HealthController(HealthUseCase healthUseCase) {
        this.healthUseCase = healthUseCase;
    }

    /**
     * Retorna o status de saúde da aplicação.
     * Endpoint público para monitoramento.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        String status = healthUseCase.check();
        return ResponseEntity.ok(Map.of("status", status));
    }
}
