package com.planifio.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TacheNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErreurReponse gererTacheIntrouvable(TacheNotFoundException ex) {
        return new ErreurReponse(ex.getMessage());
    }

    @ExceptionHandler(EvenementNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErreurReponse gererEvenementIntrouvable(EvenementNotFoundException ex) {
        return new ErreurReponse(ex.getMessage());
    }

    public record ErreurReponse(String message) {}
}
