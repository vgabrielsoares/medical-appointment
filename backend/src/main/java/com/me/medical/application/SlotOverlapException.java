package com.me.medical.application;

/**
 * Exceção lançada quando um slot a ser criado/atualizado se sobrepõe a outro existente.
 */
public class SlotOverlapException extends RuntimeException {
    public SlotOverlapException(String message) {
        super(message);
    }
}
