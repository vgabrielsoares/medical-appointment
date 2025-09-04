package com.me.medical.api;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.me.medical.application.HealthUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controller para verificação de saúde da aplicação médica.
 * 
 * Fornece endpoints para monitoramento específico do sistema de agendamento médico,
 * complementando os endpoints padrão do Spring Actuator.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Verificação de saúde da aplicação")
public class HealthController {

    private final HealthUseCase healthUseCase;
    
    @Autowired
    private DataSource dataSource;

    public HealthController(HealthUseCase healthUseCase) {
        this.healthUseCase = healthUseCase;
    }

    /**
     * Retorna o status de saúde da aplicação.
     * Endpoint público para monitoramento.
     */
    @GetMapping("/health")
    @Operation(summary = "Health check básico", description = "Verifica status básico da aplicação")
    public ResponseEntity<Map<String, String>> health() {
        String status = healthUseCase.check();
        return ResponseEntity.ok(Map.of("status", status));
    }

    /**
     * Health check avançado específico para o sistema médico.
     * 
     * Verifica:
     * - Conectividade com banco de dados
     * - Performance de queries
     * - Contagem de tabelas críticas (users, doctors, patients, slots, appointments)
     * - Status geral do sistema
     */
    @GetMapping("/health/medical-system")
    @Operation(summary = "Health check do sistema médico", 
               description = "Verificação completa do sistema de agendamento médico")
    public ResponseEntity<Map<String, Object>> checkMedicalSystemHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Verificação de conectividade e performance
            Map<String, Object> dbHealth = checkDatabaseHealth();
            healthInfo.put("database", dbHealth);
            
            // Verificação de tabelas críticas
            Map<String, Object> tablesHealth = checkCriticalMedicalTables();
            healthInfo.put("medicalTables", tablesHealth);
            
            // Informações gerais
            long totalTime = System.currentTimeMillis() - startTime;
            healthInfo.put("timestamp", LocalDateTime.now().toString());
            healthInfo.put("totalCheckTime", totalTime + "ms");
            healthInfo.put("status", "UP");
            healthInfo.put("component", "Medical Appointment System");
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            healthInfo.put("status", "DOWN");
            healthInfo.put("error", e.getMessage());
            healthInfo.put("timestamp", LocalDateTime.now().toString());
            healthInfo.put("component", "Medical Appointment System");
            
            return ResponseEntity.status(503).body(healthInfo);
        }
    }

    /**
     * Verifica conectividade e performance do banco de dados
     */
    private Map<String, Object> checkDatabaseHealth() throws Exception {
        Map<String, Object> dbInfo = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {
            
            long responseTime = System.currentTimeMillis() - startTime;
            
            if (resultSet.next() && resultSet.getInt(1) == 1) {
                dbInfo.put("status", "UP");
                dbInfo.put("type", "PostgreSQL");
                dbInfo.put("responseTime", responseTime + "ms");
                dbInfo.put("connectionPool", "HikariCP");
                
                if (responseTime > 1000) {
                    dbInfo.put("warning", "Slow database response detected");
                }
            } else {
                dbInfo.put("status", "DOWN");
                dbInfo.put("error", "Unexpected query result");
            }
        }
        
        return dbInfo;
    }

    /**
     * Verifica tabelas críticas do sistema médico
     */
    private Map<String, Object> checkCriticalMedicalTables() throws Exception {
        Map<String, Object> tablesInfo = new HashMap<>();
        
        try (Connection connection = dataSource.getConnection()) {
            // Verificar tabela users (autenticação)
            int userCount = getTableCount(connection, "users");
            tablesInfo.put("users", Map.of("count", userCount, "description", "Sistema de autenticação"));
            
            // Verificar tabela doctors (médicos)
            int doctorCount = getTableCount(connection, "doctors");
            tablesInfo.put("doctors", Map.of("count", doctorCount, "description", "Cadastro de médicos"));
            
            // Verificar tabela patients (pacientes)
            int patientCount = getTableCount(connection, "patients");
            tablesInfo.put("patients", Map.of("count", patientCount, "description", "Cadastro de pacientes"));
            
            // Verificar tabela slots (horários disponíveis)
            int slotCount = getTableCount(connection, "slots");
            tablesInfo.put("slots", Map.of("count", slotCount, "description", "Horários médicos disponíveis"));
            
            // Verificar tabela appointments (agendamentos)
            int appointmentCount = getTableCount(connection, "appointments");
            tablesInfo.put("appointments", Map.of("count", appointmentCount, "description", "Agendamentos realizados"));
            
            tablesInfo.put("status", "All critical medical tables accessible");
            
        } catch (Exception e) {
            tablesInfo.put("status", "ERROR");
            tablesInfo.put("error", "Could not verify all medical tables: " + e.getMessage());
        }
        
        return tablesInfo;
    }

    /**
     * Conta registros em uma tabela específica
     */
    private int getTableCount(Connection connection, String tableName) throws Exception {
        String sql = "SELECT COUNT(*) FROM " + tableName;
        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return -1;
        }
    }
}
