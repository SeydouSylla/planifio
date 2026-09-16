package com.planifio.exception;


public class EvenementNotFoundException extends RuntimeException {
    public EvenementNotFoundException(Long id) {
        super("Événement introuvable : " + id);
    }
}
