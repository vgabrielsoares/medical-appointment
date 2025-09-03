package com.me.medical.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de domínio representando um médico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
    private UUID id;
    private UUID userId;
    private String name;
    private String specialty;
}
