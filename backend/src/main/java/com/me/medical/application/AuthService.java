package com.me.medical.application;

import java.util.Map;

public interface AuthService {
    Map<String, Object> login(String email, String password);
}
