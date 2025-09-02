package com.me.medical.application;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String email, String password);
    Map<String, Object> register(String name, String email, String password, String role, String specialty, String phone);
    boolean emailExists(String email);
}
