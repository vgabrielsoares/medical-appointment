package com.me.medical.application;

/**
 * Use-case simples para prover um ponto de integração entre a camada API e
 * as regras de negócio (exemplo de scaffold da camada `application`).
 *
 * Esta interface é deliberadamente livre de dependências de framework
 * para preservar a separação proposta pela Clean Architecture.
 */
public interface HealthUseCase {
    /**
     * Retorna uma string simples representando o estado da aplicação.
     * @return "ok" quando a aplicação estiver saudável
     */
    String check();
}
