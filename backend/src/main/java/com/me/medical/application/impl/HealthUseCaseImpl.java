package com.me.medical.application.impl;

import com.me.medical.application.HealthUseCase;

/** Implementação trivial usada pelo controller de health. */
public class HealthUseCaseImpl implements HealthUseCase {
    @Override
    public String check() {
        return "ok";
    }
}
