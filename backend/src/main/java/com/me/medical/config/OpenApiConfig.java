package com.me.medical.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do OpenAPI/Swagger para documentação automática da API.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Medical Appointment (MedBook)")
                        .description("""
                            API para sistema de agendamento médico.
                            
                            **Funcionalidades principais:**
                            - Médicos cadastram e gerenciam horários disponíveis
                            - Pacientes visualizam horários e realizam agendamentos
                            - Autenticação JWT com roles (ROLE_DOCTOR, ROLE_PATIENT)
                            
                            **Credenciais para teste:**
                            - Médico: `doctor@example.com` / `doctorpass`
                            - Paciente: `patient@example.com` / `patientpass`
                            """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Victor Gabriel Soares")
                                .email("4vgabriel@gmail.com")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Token JWT obtido através do endpoint /api/auth/login")));
    }

    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            enhanceAuthEndpoints(openApi);
            enhanceAppointmentEndpoints(openApi);
            enhanceSlotEndpoints(openApi);
            enhanceCommonResponses(openApi);
        };
    }

    private void enhanceAuthEndpoints(OpenAPI openApi) {
        var paths = openApi.getPaths();
        
        // Login endpoint
        if (paths.get("/api/auth/login") != null) {
            var loginPost = paths.get("/api/auth/login").getPost();
            if (loginPost != null) {
                loginPost.summary("Realizar login")
                        .description("Autentica um usuário e retorna token JWT")
                        .addTagsItem("Autenticação");
                
                // Adicionar exemplo de request
                if (loginPost.getRequestBody() != null && 
                    loginPost.getRequestBody().getContent() != null &&
                    loginPost.getRequestBody().getContent().get("application/json") != null) {
                    
                    var mediaType = loginPost.getRequestBody().getContent().get("application/json");
                    mediaType.addExamples("doctor", new Example()
                            .summary("Login como médico")
                            .value("{\n  \"email\": \"doctor@example.com\",\n  \"password\": \"doctorpass\"\n}"));
                    mediaType.addExamples("patient", new Example()
                            .summary("Login como paciente")
                            .value("{\n  \"email\": \"patient@example.com\",\n  \"password\": \"patientpass\"\n}"));
                }
            }
        }

        // Register endpoint
        if (paths.get("/api/auth/register") != null) {
            var registerPost = paths.get("/api/auth/register").getPost();
            if (registerPost != null) {
                registerPost.summary("Registrar novo usuário")
                        .description("Registra um novo usuário no sistema")
                        .addTagsItem("Autenticação");
            }
        }
    }

    private void enhanceAppointmentEndpoints(OpenAPI openApi) {
        var paths = openApi.getPaths();
        
        // Create appointment
        if (paths.get("/api/appointments") != null && paths.get("/api/appointments").getPost() != null) {
            var createPost = paths.get("/api/appointments").getPost();
            createPost.summary("Criar agendamento")
                    .description("Cria um novo agendamento para o paciente autenticado")
                    .addTagsItem("Agendamentos");
        }

        // List my appointments
        if (paths.get("/api/appointments/my") != null && paths.get("/api/appointments/my").getGet() != null) {
            var listGet = paths.get("/api/appointments/my").getGet();
            listGet.summary("Listar meus agendamentos")
                    .description("Lista todos os agendamentos do paciente autenticado")
                    .addTagsItem("Agendamentos");
        }
    }

    private void enhanceSlotEndpoints(OpenAPI openApi) {
        var paths = openApi.getPaths();
        
        paths.entrySet().stream()
            .filter(entry -> entry.getKey().matches("/api/doctors/\\{doctorId\\}/slots.*"))
            .forEach(entry -> {
                var pathItem = entry.getValue();
                
                if (pathItem.getPost() != null) {
                    pathItem.getPost()
                            .summary("Criar slot")
                            .description("Cria um novo horário disponível para o médico")
                            .addTagsItem("Slots");
                }
                
                if (pathItem.getGet() != null) {
                    pathItem.getGet()
                            .summary("Listar slots")
                            .description("Lista horários do médico (todos para o próprio médico, apenas disponíveis para outros)")
                            .addTagsItem("Slots");
                }
                
                if (pathItem.getPut() != null) {
                    pathItem.getPut()
                            .summary("Atualizar slot")
                            .description("Atualiza um horário específico do médico")
                            .addTagsItem("Slots");
                }
                
                if (pathItem.getDelete() != null) {
                    pathItem.getDelete()
                            .summary("Excluir slot")
                            .description("Remove um horário do médico")
                            .addTagsItem("Slots");
                }
            });
    }

    private void enhanceCommonResponses(OpenAPI openApi) {
        var components = openApi.getComponents();
        if (components == null) {
            components = new Components();
            openApi.setComponents(components);
        }

        // Adicionar responses comuns
        components.addResponses("Unauthorized", new ApiResponse()
                .description("Token JWT inválido ou ausente")
                .content(new Content().addMediaType("application/json", 
                    new MediaType().schema(new Schema<>().type("object")
                        .addProperty("error", new Schema<>().type("string").example("Unauthorized"))
                        .addProperty("message", new Schema<>().type("string").example("Token inválido"))))));

        components.addResponses("Forbidden", new ApiResponse()
                .description("Acesso negado - usuário não tem permissão")
                .content(new Content().addMediaType("application/json", 
                    new MediaType().schema(new Schema<>().type("object")
                        .addProperty("error", new Schema<>().type("string").example("Forbidden"))
                        .addProperty("message", new Schema<>().type("string").example("Acesso negado"))))));

        components.addResponses("NotFound", new ApiResponse()
                .description("Recurso não encontrado")
                .content(new Content().addMediaType("application/json", 
                    new MediaType().schema(new Schema<>().type("object")
                        .addProperty("error", new Schema<>().type("string").example("Not Found"))
                        .addProperty("message", new Schema<>().type("string").example("Recurso não encontrado"))))));
    }
}
