package com.planifio.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/*
 Spring Security publie ces événements automatiquement à chaque tentative
 d'authentification - aucune modification du flux de connexion lui-même
 n'est nécessaire, on se contente d'écouter ce qui se passe déjà.
 */
@Component
@RequiredArgsConstructor
public class EvenementsAuthentificationListener {

    private final ConnexionSecuriteService securiteService;

    
     // AbstractAuthenticationFailureEvent couvre tout type d'échec ,
     // mauvais mot de passe, mais aussi une tentative sur un compte déjà
     // verrouillé, sans avoir à distinguer chaque sous-type un par un.
     
    @EventListener
    public void surEchecAuthentification(AbstractAuthenticationFailureEvent evenement) {
        String email = evenement.getAuthentication().getName();
        securiteService.enregistrerEchec(email);
    }

    @EventListener
    public void surReussiteAuthentification(AuthenticationSuccessEvent evenement) {
        String email = evenement.getAuthentication().getName();
        securiteService.enregistrerReussite(email);
    }
}
