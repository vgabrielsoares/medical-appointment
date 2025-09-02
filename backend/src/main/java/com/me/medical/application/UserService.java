package com.me.medical.application;

import java.util.Map;

/**
 * Service responsável por gerenciar perfis de usuário.
 */
public interface UserService {
    
    /**
     * Retorna dados do perfil do usuário.
     * 
     * @param userEmail email do usuário
     * @return mapa com dados do perfil
     */
    Map<String, Object> getProfile(String userEmail);
    
    /**
     * Atualiza dados do perfil do usuário.
     * 
     * @param userEmail email do usuário
     * @param profileData dados a serem atualizados
     * @return mapa com dados atualizados
     */
    Map<String, Object> updateProfile(String userEmail, Map<String, Object> profileData);
}
