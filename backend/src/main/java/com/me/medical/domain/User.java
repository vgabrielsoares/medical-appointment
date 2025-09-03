package com.me.medical.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio representando um usuário do sistema.
 *
 * Esta classe é intencionalmente simples e sem dependências de framework
 * para preservar as camadas internas da Clean Architecture.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String email;
    private String role;
}
