package com.me.medical.api;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Utilitários relacionados a Authentication usados pelos controllers.
 */
public final class AuthUtils {
    private AuthUtils() {
    }

    /**
     * Converte Authentication.getPrincipal() (esperado como String UUID) para UUID.
     * Retorna null se a conversão falhar ou se auth for nulo.
     */
    public static UUID authUserId(Authentication auth) {
        if (auth == null)
            return null;
        try {
            return UUID.fromString((String) auth.getPrincipal());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Verifica se o Authentication contém a role informada (ex: "ROLE_DOCTOR").
     */
    public static boolean hasRole(Authentication auth, String role) {
        if (auth == null)
            return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if (role.equals(ga.getAuthority()))
                return true;
        }
        return false;
    }

    public static boolean isDoctor(Authentication auth) {
        return hasRole(auth, "ROLE_DOCTOR");
    }

    public static boolean isPatient(Authentication auth) {
        return hasRole(auth, "ROLE_PATIENT");
    }
}
