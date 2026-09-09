package com.planifio.service;

import com.planifio.dto.InscriptionForm;
import com.planifio.model.Utilisateur;
import com.planifio.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class InscriptionService {

    private final UtilisateurRepository repository;
    private final PasswordEncoder passwordEncoder;

    public static class EmailDejaUtiliseException extends RuntimeException {
        public EmailDejaUtiliseException() {
            super("Cet email est déjà associé à un compte");
        }
    }

    public Utilisateur inscrire(InscriptionForm form) {
        if (repository.existsByEmail(form.getEmail())) {
            throw new EmailDejaUtiliseException();
        }
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(form.getEmail());
        utilisateur.setNom(form.getNom());
        // Le mot de passe en clair du formulaire ne survit jamais au-delà
        // de cette ligne - seul le hash BCrypt est conservé.
        utilisateur.setMotDePasseHache(passwordEncoder.encode(form.getMotDePasse()));
        return repository.save(utilisateur);
    }
}
